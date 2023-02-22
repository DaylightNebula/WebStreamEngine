package webstreamengine.server

import io.ktor.network.tls.certificates.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.*
import java.io.*
import java.security.KeyStore

fun main(args: Array<String>) {

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
        keyStore
    } else KeyStore.getInstance("JKS").apply { this.load(keyStoreFile.inputStream(), keyStoreMaster.toCharArray()) }

    // generate server environment
    val environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
        connector {
            port = 8080
        }
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

    // start the server
    embeddedServer(Netty, environment).start(wait = true)
}

// create routing for http requests
fun Application.module() {
    routing {
        get("/") {
            call.respondText("Hello, world!")
        }
    }
}