package webstreamengine.client.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import webstreamengine.client.networking.FuelClient
import java.io.File

object FontManager {
    private val fontMap = hashMapOf<String, FileHandle>()
    private val requestedIDs = mutableListOf<String>()

    private fun loadLocal(id: String, path: String): FileHandle? {
        val file = Gdx.files.absolute(path)
        fontMap[id] = file
        return file
    }

    fun makeIDExist(id: String) {
        // if we already have a font with the given id, just pass it along
        if (fontMap.containsKey(id)) {
            return
        }

        // if we have a cached file with the given id, load that
        val fontFile = File(System.getProperty("user.dir"), "cache/$id.ttf")
        if (fontFile.exists()) {
            return
        }

        // if the given id is not in the requested id list, send a request to the server
        if (!requestedIDs.contains(id)) {
            requestedIDs.add(id)
            FuelClient.requestFile(id) {}
        }
    }

    fun getFont(key: String): FileHandle? = fontMap[key] ?: loadLocal(key, "cache/$key")
}