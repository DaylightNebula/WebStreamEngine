package webstreamengine.client.networking

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.httpGet
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import webstreamengine.client.JarInterface
import java.io.File
import java.io.FileInputStream
import java.math.BigInteger
import java.security.KeyStore
import java.security.MessageDigest
import java.util.*

fun main(args: Array<String>) {
    println("Starting with args ${args.map { it }}")

    FuelClient.startClient(args)
    while(true) {}
}

object FuelClient {
    lateinit var allFiles: JSONObject

    private val cacheFolder = File("cache")
    private lateinit var serverAddr: String
    private val fileMapFile = File(cacheFolder, "fileMap.json")
    private lateinit var fileMap: JSONObject

    fun startClient(args: Array<String>) {
        // if we have a file map file, load it, otherwise create a new one
        fileMap = if (fileMapFile.exists())
            JSONObject(fileMapFile.readText())
        else
            JSONObject()

        // get the server address
        serverAddr = args.firstOrNull { it.startsWith("-serverPath=") }?.split("=")?.last()
            ?: throw IllegalArgumentException("Client must be given a -serverPath=<serverpath> argument")

        // if we are given arguments for a path to a keystore and its password, add that to the key store
        val keyStorePath = args.firstOrNull { it.startsWith("-keyStorePath=") }?.split("=")?.last()
        val keyStorePass = args.firstOrNull { it.startsWith("-keyStorePass=") }?.split("=")?.last()
        if (keyStorePath != null && keyStorePass != null) {
            val ksFile = File(keyStorePath)
            if (ksFile.exists()) {
                FuelManager.instance.keystore = KeyStore.getInstance("JKS").apply {
                    this.load(FileInputStream(ksFile), keyStorePass.toCharArray())
                }
            }
        }

        // download all files list
        runBlocking {
            allFiles = Fuel.get("$serverAddr/allfiles")
                .awaitResult(deserializable = object : Deserializable<JSONObject> {
                    override fun deserialize(response: Response): JSONObject {
                        return JSONObject(String(response.data))
                    }
                }).get()
        }
    }

    private fun getPreexistingFile(fileName: String): File? {
        if (!fileMap.has(fileName) || !allFiles.has(fileName)) return null

        // get all files hash
        val allFile = allFiles.getJSONObject(fileName)
        val serverFileHash = allFile.getBigInteger("hash")

        // load file map json
        val json = fileMap.getJSONObject(fileName)
        val curHash = json.getBigInteger("hash")

        // if the current hash is equivalent, return the file, otherwise, return nothing
        val file = File(cacheFolder, allFile.getString("localPath"))
        return if (serverFileHash == curHash)
                file
            else {
                if (file.exists()) file.delete()
                null
            }
    }

    fun requestFileBlocking(fileName: String): File {
        // if we have a matching pre-exsting file, return that
        val preexisting = getPreexistingFile(fileName)
        if (preexisting != null) return preexisting

        // send a request for the file
        val localPath = allFiles.getJSONObject(fileName).getString("localPath")
        val targetFile = File(cacheFolder, localPath)
        targetFile.parentFile.mkdirs()
        runBlocking {
            val json = "$serverAddr/file".httpGet(listOf(*(JarInterface.currentApp?.getBaseHTTPParams() ?: arrayOf()), Pair("file", fileName)))
                .awaitResult(deserializable = object : Deserializable<JSONObject>{
                    override fun deserialize(response: Response): JSONObject {
                        return JSONObject(String(response.data))
                    }
                }).get()
            val bytes = Base64.getDecoder().decode(json.getString("bytes")!!)
            targetFile.parentFile.mkdir()
            targetFile.writeBytes(bytes)
            fileMap.put(
                fileName,
                JSONObject()
                    .put("id", fileName)
                    .put("path", localPath)
                    .put("hash", BigInteger(1, MessageDigest.getInstance("MD5").digest(bytes)))
            )
            fileMapFile.writeBytes(fileMap.toString(1).toByteArray())
        }
        return targetFile
    }

    fun requestFile(fileName: String, onComplete: (file: File) -> Unit) {
        // if we have a matching pre-existing file, return that
        val preexisting = getPreexistingFile(fileName)
        if (preexisting != null) { onComplete(preexisting); return }

        // send request to server for the file
        val localPath = allFiles.getJSONObject(fileName).getString("localPath")
        "$serverAddr/file".httpGet(listOf(*(JarInterface.currentApp?.getBaseHTTPParams() ?: arrayOf()), Pair("file", fileName))).response { _, response, _ ->
            val data = JSONObject(String(response.data))
            val file = File(cacheFolder, localPath)
            val bytes = Base64.getDecoder().decode(data.getString("bytes")!!)
            file.parentFile.mkdirs()
            file.writeBytes(bytes)
            fileMap.put(
                fileName,
                JSONObject()
                    .put("id", fileName)
                    .put("path", localPath)
                    .put("hash", BigInteger(1, MessageDigest.getInstance("MD5").digest(bytes)))
            )
            fileMapFile.writeBytes(fileMap.toString(1).toByteArray())
            onComplete(file)
        }
    }
}