package webstreamengine.server

import webstreamengine.core.ByteUtils
import java.io.File

object FileHandler {

    val modelFiles = hashMapOf<String, ByteArray>()
    lateinit var jarFile: ByteArray

    fun init() {
        loadFilesRecursively(File(System.getProperty("user.dir"), "assets"))
    }

    fun loadFilesRecursively(root: File) {
        root.listFiles()?.forEach {
            if (it.isDirectory) loadFilesRecursively(root)
            else when(it.extension) {
                "g3dj" -> loadModel(it.nameWithoutExtension, it)
                "jar" -> loadJarFile(it)
            }
        }
    }

    fun loadModel(id: String, file: File) {
        modelFiles[id] = byteArrayOf(
            *ByteUtils.convertStringToByteArray(id),
            *ByteUtils.convertByteArrayToByteArray(file.readBytes())
        )
        println("Loaded model file ${file.absolutePath}")
    }

    fun loadJarFile(file: File) {
        jarFile = ByteUtils.convertByteArrayToByteArray(file.readBytes())
        println("Loaded jar file ${file.absolutePath}")
    }
}