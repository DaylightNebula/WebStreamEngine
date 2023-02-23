package webstreamengine.server

import io.ktor.network.tls.certificates.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.json.JSONObject
import org.slf4j.*
import java.io.*
import java.math.BigInteger
import java.security.KeyStore
import java.security.MessageDigest
import java.util.*
import java.util.zip.ZipFile

val fileMap = JSONObject()
val assetsFolder = File("assets")

fun main(args: Array<String>) {
    println("Starting with args ${args.map { it }}")

    FBXToG3DJConverter.init()

    // load assets
    val start = System.currentTimeMillis()
    loadAssets(assetsFolder)
    val end = System.currentTimeMillis()
    println("Loaded assets from $assetsFolder in ${end - start}MS")

    // get the key store path
    val keyStoreFilePath = args.firstOrNull { it.startsWith("-keyStorePath=") }?.split("=")?.last()
        ?: throw IllegalArgumentException("A key store path is required to start")

    // get key store generation information
    val genKeyStore = args.firstOrNull { it.startsWith("-genKeyStore=") }?.split("=")?.last()?.toBooleanStrictOrNull()
        ?: false
    val keyStoreAlias = args.firstOrNull { it.startsWith("-keyStoreAlias=") }?.split("=")?.last()
        ?: throw IllegalArgumentException("Key store alias must be given as argument -keyStoreAlias")
    val keyStorePass = args.firstOrNull { it.startsWith("-keyStorePass=") }?.split("=")?.last()
        ?: throw IllegalArgumentException("Key store password must be given as argument -keyStorePass")
    val keyStoreMaster = args.firstOrNull { it.startsWith("-keyStoreMaster=") }?.split("=")?.last()
        ?: throw IllegalArgumentException("Key store master password must be given as argument -keyStoreMaster")

    // load key store file and its key store
    val keyStoreFile = File(keyStoreFilePath)
    val keyStore = if (genKeyStore) {
        val keyStore = buildKeyStore {
            certificate(keyStoreAlias) {
                password = keyStorePass
                domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
            }
        }
        keyStore.saveToFile(keyStoreFile, keyStoreMaster)
        println("Generated KeyStore")
        keyStore
    } else KeyStore.getInstance("JKS").apply { this.load(keyStoreFile.inputStream(), keyStoreMaster.toCharArray()) }

    // generate server environment
    val environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
//        connector {
//            port = 8080
//        }
        sslConnector(
            keyStore = keyStore,
            keyAlias = keyStoreAlias,
            keyStorePassword = { keyStoreMaster.toCharArray() },
            privateKeyPassword = { keyStorePass.toCharArray() }
        ) {
            port = 8443
            keyStorePath = keyStorePath
        }
        module(Application::module)
    }
    println("Created server environments")

    // start the server
    embeddedServer(Netty, environment).start(wait = true)
}

fun loadAssets(rootFolder: File) {
    // load all files recursively
    recursivelyLoadFiles(rootFolder)

    // save fileMap.json
    File(assetsFolder, "fileMap.json").writeText(fileMap.toString(1))
}

fun recursivelyLoadFiles(rootFile: File) {
    rootFile.listFiles()?.forEach { file ->
        if (file.isDirectory) {
            recursivelyLoadFiles(file)
            return@forEach
        }

        // ignore any files named "fileMap.json"
        if (file.name.equals("fileMap.json")) return@forEach

        // load file bytes, with some changes for some file types
        val (fileBytes, fileName) = when(file.extension) {
            "fbx" -> {
                FBXToG3DJConverter.convertFile(file)
                Pair(File(file.path.replace("fbx", "g3dj")).readBytes(), file.name.replace("fbx", "g3dj"))
            }
            else -> {
                Pair(file.readBytes(), file.name)
            }
        }

        // generate hash for the file
        val hash = BigInteger(1, MessageDigest.getInstance("MD5").digest(fileBytes))

        // save to file map json
        fileMap.put(fileName, JSONObject().put("id", fileName).put("hash", hash).put("type", file.extension).put("localPath", file.path.removePrefix("assets\\")))
    }
}

// create routing for http requests
fun Application.module() {
    routing {
        get("/allfiles") {
            call.respondText(fileMap.toString(1))
        }
        get("/file") {
            val fileParam = call.parameters["file"]
            val srcJson = fileMap.getJSONObject(fileParam)

            if (srcJson == null) {
                println("WARNING: Client requested file $fileParam which does not exist")
                call.respondText("NO_FILE")
                return@get
            }

            // send the file back
            val bytes = File(assetsFolder, srcJson.getString("localPath")).readBytes()
            val json = JSONObject()
                        .put("id", srcJson.getString("id"))
                        .put("hash", srcJson.getBigInteger("hash"))
                        .put("bytes", Base64.getEncoder().encodeToString(bytes))
            call.respondText(json.toString(0))
        }
    }
}