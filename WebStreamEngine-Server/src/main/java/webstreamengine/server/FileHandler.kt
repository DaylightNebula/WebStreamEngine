package webstreamengine.server

import webstreamengine.core.ByteUtils
import java.io.File

object FileHandler {

    val modelFiles = hashMapOf<String, ByteArray>()
    val imageFiles = hashMapOf<String, ByteArray>()
    val mp3Files = hashMapOf<String, ByteArray>()
    val wavFiles = hashMapOf<String, ByteArray>()
    val oggFiles = hashMapOf<String, ByteArray>()
    lateinit var jarFile: ByteArray

    val rootDir = File(System.getProperty("user.dir"))
    val rootDirLength = rootDir.absolutePath.length

    fun init() {
        // setup converters
        FBXToG3DJConverter.init()

        // start file loading
        loadFilesRecursively(File(rootDir, "assets"))
    }

    fun loadFilesRecursively(root: File) {
        // loop through all children file
        root.listFiles()?.forEach {
            // if this file is a directory, load all files from that directory
            if (it.isDirectory) loadFilesRecursively(root)
            // otherwise, load files by their extension appropriately
            else when(it.extension) {
                "g3dj" -> loadModel(it.nameWithoutExtension, it)
                "fbx" -> loadModelWithFBXConversion(it.nameWithoutExtension, it)
                "jar" -> loadJarFile(it)
                "png" -> loadImage(it.nameWithoutExtension, it)
                "jpg" -> loadImage(it.nameWithoutExtension, it)
                "mp3" -> loadMP3(it.nameWithoutExtension, it)
                "wav" -> loadWav(it.nameWithoutExtension, it)
                "ogg" -> loadOgg(it.nameWithoutExtension, it)
            }
        }
    }

    fun getLocalPath(file: File): String {
        val absPath = file.absolutePath
        return absPath.substring(rootDirLength + 1, absPath.length)
    }

    //.\fbx-conv.exe -o g3dj -f -v assets\barracks.fbx assets\barracks.g3dj
    fun loadModelWithFBXConversion(id: String, file: File) {
        // if a model already exists with this id, cancel
        if (modelFiles.containsKey(id)) return

        // make sure fbx to G3DJ converter is active
        if (!FBXToG3DJConverter.active) {
            System.err.println("FBX to G3DJ converter not properly setup, could not convert and load ${file.absolutePath}")
            return
        }

        // get target files and their local paths
        val targetLocalPath = getLocalPath(file)
        val finalFile = File(file.absolutePath.replace("fbx", "g3dj"))
        val finalFileLocalPath = getLocalPath(finalFile)

        // if a final file already exists, cancel
        if (finalFile.exists()) return

        // run command to convert the given file
        println("Converting FBX to G3DJ ${file.absolutePath}")
        val builder = ProcessBuilder("cmd.exe", "/c", ".\\fbx-conv.exe -o g3dj -f -v $targetLocalPath $finalFileLocalPath")
        builder.redirectErrorStream(true)
        val process = builder.start()

        // wait for the conversion process to end
        process.waitFor()

        // load final model
        loadModel(id, finalFile)
    }

    fun loadModel(id: String, file: File) {
        if (modelFiles.containsKey(id)) return
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

    fun loadImage(id: String, file: File) {
        if (imageFiles.containsKey(id)) return
        imageFiles[id] = byteArrayOf(
            *ByteUtils.convertStringToByteArray(id),
            *ByteUtils.convertByteArrayToByteArray(file.readBytes())
        )
        println("Loaded image file ${file.absolutePath}")
    }

    fun loadMP3(id: String, file: File) {
        if (mp3Files.containsKey(id)) return
        mp3Files[id] = byteArrayOf(
            *ByteUtils.convertStringToByteArray(id),
            *ByteUtils.convertByteArrayToByteArray(file.readBytes())
        )
        println("Loaded mp3 file ${file.absolutePath}")
    }

    fun loadWav(id: String, file: File) {
        if (wavFiles.containsKey(id)) return
        wavFiles[id] = byteArrayOf(
            *ByteUtils.convertStringToByteArray(id),
            *ByteUtils.convertByteArrayToByteArray(file.readBytes())
        )
        println("Loaded mp3 file ${file.absolutePath}")
    }

    fun loadOgg(id: String, file: File) {
        if (oggFiles.containsKey(id)) return
        oggFiles[id] = byteArrayOf(
            *ByteUtils.convertStringToByteArray(id),
            *ByteUtils.convertByteArrayToByteArray(file.readBytes())
        )
        println("Loaded mp3 file ${file.absolutePath}")
    }
}