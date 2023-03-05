package webstreamengine.server

import org.json.JSONObject
import java.io.File
import java.math.BigInteger
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.*
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.security.MessageDigest

class FilesThread: Thread() {

    private val assembledJson = JSONObject()
    private var jsonDirty = false
    private val watcher = FileSystems.getDefault().newWatchService()
    private val dirs = hashMapOf<WatchKey, MutableList<AssetFile>>()

    init {
        loadFilesRecursively(assetsFolder)
    }

    private fun loadFilesRecursively(root: File) {
        val files = mutableListOf<AssetFile>()
        root.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                loadFilesRecursively(file)
                return@forEach
            }

            // ignore any files named "fileMap.json"
            if (file.name.equals("fileMap.json")) return@forEach

            val assetFile = convertToAssetFile(file, false)
            files.add(assetFile)
            addOrUpdateAssetFileToJson(assetFile)
        }
        dirs[root.toPath().register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)] = files
    }

    private fun convertToAssetFile(file: File, slow: Boolean): AssetFile {
        if (slow) sleep(10)

        // load file bytes, with some changes for some file types
        val (finalFile, fileBytes) = when(file.extension) {
            "fbx" -> {
                FBXToG3DJConverter.convertFile(file)
                val f = File(file.path.replace("fbx", "g3dj"))
                Pair(f, f.readBytes())
            }
            else -> {
                Pair(file, file.readBytes())
            }
        }

        // generate hash for the file
        val hash = BigInteger(1, MessageDigest.getInstance("MD5").digest(fileBytes))

        // convert to asset file
        return AssetFile(finalFile, hash)
    }

    fun getJson(): JSONObject = assembledJson

    private fun addOrUpdateAssetFileToJson(assetFile: AssetFile) {
        // remove old copy if one exists
        if (assembledJson.has(assetFile.file.name))
            assembledJson.remove(assetFile.file.name)

        // log it
        println("ADD OR UPDATE ${assetFile.file.name} with hash ${assetFile.hash}")

        // add asset file to json
        assembledJson.put(
            assetFile.file.name,
            JSONObject()
                .put("id", assetFile.file.name)
                .put("hash", assetFile.hash)
                .put("type", assetFile.file.extension)
                .put("localPath", assetFile.file.path.removePrefix("assets\\"))
        )
        jsonDirty = true
    }

    override fun run() {
        while (true) {
            // get start time
            val start = System.currentTimeMillis()

            // check if any watch keys have changed
            val toRemove = hashMapOf<File, WatchKey>()
            val toUpdate = hashMapOf<File, WatchKey>()
            dirs.forEach { (watchKey, fileList) ->
                // loop though all events
                watchKey.pollEvents().forEach { e ->
                    // get kind
                    val kind = e.kind()

                    // skip overflow kind
                    if (kind == OVERFLOW) return@forEach

                    // convert to event with path
                    val event = e as WatchEvent<Path>
                    val filePath = event.context()
                    val file = filePath.toFile()
                    if (file.name == "fileMap.json") return@forEach

                    if (toRemove.containsKey(file)) return@forEach

                    if (kind == ENTRY_DELETE) {
                        toRemove[file] = watchKey
                        return@forEach
                    }

                    if (toUpdate.containsKey(file)) return@forEach

                    toUpdate[file] = watchKey
                }
            }

            toRemove.forEach { (file, key) ->
                assembledJson.remove(file.name)
                dirs[key]?.removeAll { it.file == file }
                jsonDirty = true
                println("REMOVED ${file.name}")
            }

            toUpdate.forEach { (f, key) ->
                val file = File("assets/" + f.path)
                val assetFile = convertToAssetFile(file, true)
                dirs[key]?.removeAll { it.file == file }
                dirs[key]?.add(assetFile)
                addOrUpdateAssetFileToJson(assetFile)
            }

            File("assets/fileMap.json").writeText(assembledJson.toString(1))

            // wait 50 ms
            val diff = 50 - (System.currentTimeMillis() - start)
            if (diff in 1..49)
                sleep(diff)
        }
    }
}
data class AssetFile(val file: File, val hash: BigInteger)