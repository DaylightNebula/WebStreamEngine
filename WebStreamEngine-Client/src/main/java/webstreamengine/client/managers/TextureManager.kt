package webstreamengine.client.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import webstreamengine.client.FuelClient
import webstreamengine.client.conn
import webstreamengine.client.ui.microelements.ImageElement
import webstreamengine.core.ByteUtils
import webstreamengine.core.PacketType
import webstreamengine.core.PacketUtils
import java.io.File

object TextureManager {

    private val textureMap = hashMapOf<String, Texture>()
    private val requestedIDs = mutableListOf<String>()
    private val waitingForTexture = hashMapOf<String, MutableList<Any>>()

    fun loadLocal(id: String, path: String): Texture? {
        val file = Gdx.files.absolute(path)
        if (!file.exists()) return null
        val texture = Texture(file)
        textureMap[id] = texture
        return texture
    }

    fun generateSimpleTexture(id: String, width: Int, height: Int, render: (pixmap: Pixmap) -> Unit) {
        // make sure this is an empty id
        if (textureMap.containsKey(id)) return

        // generate the pix map
        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
        render(pixmap)

        // convert pix map to texture
        val texture = Texture(pixmap)

        // dispose of the pix map
        pixmap.dispose()

        // save texture
        textureMap[id] = texture
    }

    fun requestTextureIfNecessary(key: String) {
        // if we already have a texture with the given id, just pass it along
        if (textureMap.containsKey(key)) return

        // check if the cache has a file for the given id, if so load that
        val textureFile = File(System.getProperty("user.dir"), "cache/$key.img")
        if (textureFile.exists()) return

        // if the given id is not in the requested id list, send a request to the server
        if (!requestedIDs.contains(key)) {
            requestedIDs.add(key)
            FuelClient.requestFile(key) { }
        }
    }

    fun getTextureIfAvailable(key: String): Texture? = textureMap[key] ?: loadLocal(key, "cache/$key.png")

    fun dispose() {
        textureMap.values.forEach { it.dispose() }
    }
}