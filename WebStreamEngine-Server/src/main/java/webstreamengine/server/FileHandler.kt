package webstreamengine.server

import webstreamengine.core.ByteUtils
import java.io.File

object FileHandler {

    val modelFiles = hashMapOf<String, ByteArray>()
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
}