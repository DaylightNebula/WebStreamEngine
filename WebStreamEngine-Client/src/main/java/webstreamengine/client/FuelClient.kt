package webstreamengine.client

import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.httpGet
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.math.BigInteger
import java.security.KeyStore
import java.security.MessageDigest

fun main(args: Array<String>) {
    FuelClient.startClient(args)
    while(true) {}
}

object FuelClient {
    private val cacheFolder = File("cache")
    private lateinit var serverAddr: String
    private lateinit var allFiles: JSONObject
    private val fileMapFile = File(cacheFolder, "fileMap.json")
    private lateinit var fileMap: JSONObject

    fun startClient(args: Array<String>) {
        println("Starting with args ${args.map { it }}")

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
        "${serverAddr}/allfiles".httpGet().response { _, response, _ ->
            allFiles = JSONObject(String(response.data))
            downloadRequired()
            requestFileBlocking("barracks.g3dj")
        }
    }

    private fun downloadRequired() {
        allFiles.keys().forEach { key ->
            val extension = key.split(".").last()
            val required = extension == "class"
            if (required) requestFileBlocking(key)
        }
    }

    private fun getPreexistingFile(fileName: String): File? {
        if (!fileMap.has(fileName) || !allFiles.has(fileName)) return null

        // get all files hash
        val serverFileHash = allFiles.getJSONObject(fileName).getBigInteger("hash")

        // load file map json
        val json = fileMap.getJSONObject(fileName)
        val curHash = fileMap.getBigInteger("hash")

        // if the current hash is equivalent, return the file, otherwise, return nothing
        val file = File(cacheFolder, fileName)
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
        val targetFile = File(cacheFolder, fileName)
        runBlocking {
            val json = "${serverAddr}/file".httpGet(listOf(Pair("file", fileName)))
                .awaitResult(deserializable = object : Deserializable<JSONObject>{
                    override fun deserialize(response: Response): JSONObject {
                        return JSONObject(String(response.data))
                    }
                }).get()
            val bytes = json.getString("bytes")!!.toByteArray()
            targetFile.writeBytes(bytes)
            fileMap.put(
                fileName,
                JSONObject()
                    .put("id", fileName)
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
        "${serverAddr}/file".httpGet(listOf(Pair("file", fileName))).response { _, response, _ ->
            val data = JSONObject(String(response.data))
            val file = File(cacheFolder, fileName)
            val bytes = data.getString("bytes").toByteArray()
            file.writeBytes(bytes)
            fileMap.put(
                fileName,
                JSONObject()
                    .put("id", fileName)
                    .put("hash", BigInteger(1, MessageDigest.getInstance("MD5").digest(bytes)))
            )
            fileMapFile.writeBytes(fileMap.toString(1).toByteArray())
            onComplete(file)
        }
    }
}