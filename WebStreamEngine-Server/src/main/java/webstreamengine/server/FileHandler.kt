package webstreamengine.server

import webstreamengine.core.ByteUtils
import java.io.File
import java.nio.file.StandardWatchEventKinds
import java.nio.file.StandardWatchEventKinds.OVERFLOW
import java.util.concurrent.TimeUnit

object FileHandler {

    val modelFiles = hashMapOf<String, ByteArray>()
    val imageFiles = hashMapOf<String, ByteArray>()
    val mp3Files = hashMapOf<String, ByteArray>()
    val wavFiles = hashMapOf<String, ByteArray>()
    val oggFiles = hashMapOf<String, ByteArray>()
    var jarFile = byteArrayOf()

    private val rootDir = File(System.getProperty("user.dir"))
    private val rootDirLength = rootDir.absolutePath.length

    private val watchService = rootDir.toPath().fileSystem.newWatchService()

    fun init() {
        // setup converters
        FBXToG3DJConverter.init()

        beginLoad(true)
    }

    private fun beginLoad(includeWatch: Boolean) {
        // start file loading
        val assetsFile = File(rootDir, "assets")
        loadFilesRecursively(assetsFile, includeWatch)
        if (includeWatch) addToWatch(assetsFile)
    }

    fun update() {
        // get water service key
        val key = watchService.poll(0, TimeUnit.MILLISECONDS) ?: return
        var filesDirty = false

        // get events from the watch service key
        for (event in key.pollEvents()) {
            // get event kind
            val kind = event.kind()

            // if kind is overflow, skip the rest of this iteration
            if (kind == OVERFLOW) continue

            // switch for kind, for all accepted criteria set files dirty to true, if unknown criteria, print a warning statement
            when (kind) {
                StandardWatchEventKinds.ENTRY_CREATE -> { filesDirty = true }
                StandardWatchEventKinds.ENTRY_MODIFY -> { filesDirty = true }
                StandardWatchEventKinds.ENTRY_DELETE -> { filesDirty = true }
                else -> {
                    println("WARN: Unknown kind $kind")
                }
            }

            // reset the key, I don't know why we need this, but we do otherwise everything hangs
            key.reset()
        }

        // if files are dirty, reload the files
        if (filesDirty) {
            println("Reloading...")
            jarFile = byteArrayOf()
            modelFiles.clear()
            imageFiles.clear()
            mp3Files.clear()
            wavFiles.clear()
            oggFiles.clear()
            beginLoad(false)
        }
    }

    private fun addToWatch(file: File) {
        if (file.isDirectory) {
            file.toPath().register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE
            )
        }
    }

    private fun loadFilesRecursively(root: File, includeWatch: Boolean) {
        // loop through all children file
        root.listFiles()?.forEach {
            if (includeWatch) addToWatch(it)
            // if this file is a directory, load all files from that directory
            if (it.isDirectory) {
                loadFilesRecursively(root, includeWatch)
            }
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

    private fun getLocalPath(file: File): String {
        val absPath = file.absolutePath
        return absPath.substring(rootDirLength + 1, absPath.length)
    }

    //.\fbx-conv.exe -o g3dj -f -v assets\barracks.fbx assets\barracks.g3dj
    private fun loadModelWithFBXConversion(id: String, file: File) {
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

        file.delete()

        // load final model
        loadModel(id, finalFile)
    }

    private fun loadModel(id: String, file: File) {
        if (modelFiles.containsKey(id)) return
        modelFiles[id] = byteArrayOf(
            *ByteUtils.convertStringToByteArray(id),
            *ByteUtils.convertByteArrayToByteArray(file.readBytes())
        )
        println("Loaded model file ${file.absolutePath}")
    }

    private fun loadJarFile(file: File) {
        jarFile = byteArrayOf(
            *ByteUtils.convertStringToByteArray(jarMainClass),
            *ByteUtils.convertByteArrayToByteArray(file.readBytes())
        )
        println("Loaded jar file ${file.absolutePath}")
    }

    private fun loadImage(id: String, file: File) {
        if (imageFiles.containsKey(id)) return
        imageFiles[id] = byteArrayOf(
            *ByteUtils.convertStringToByteArray(id),
            *ByteUtils.convertByteArrayToByteArray(file.readBytes())
        )
        println("Loaded image file ${file.absolutePath}")
    }

    private fun loadMP3(id: String, file: File) {
        if (mp3Files.containsKey(id)) return
        mp3Files[id] = byteArrayOf(
            *ByteUtils.convertStringToByteArray(id),
            *ByteUtils.convertByteArrayToByteArray(file.readBytes())
        )
        println("Loaded mp3 file ${file.absolutePath}")
    }

    private fun loadWav(id: String, file: File) {
        if (wavFiles.containsKey(id)) return
        wavFiles[id] = byteArrayOf(
            *ByteUtils.convertStringToByteArray(id),
            *ByteUtils.convertByteArrayToByteArray(file.readBytes())
        )
        println("Loaded mp3 file ${file.absolutePath}")
    }

    private fun loadOgg(id: String, file: File) {
        if (oggFiles.containsKey(id)) return
        oggFiles[id] = byteArrayOf(
            *ByteUtils.convertStringToByteArray(id),
            *ByteUtils.convertByteArrayToByteArray(file.readBytes())
        )
        println("Loaded mp3 file ${file.absolutePath}")
    }
}