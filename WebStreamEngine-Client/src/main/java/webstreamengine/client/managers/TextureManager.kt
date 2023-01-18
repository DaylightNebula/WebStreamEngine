package webstreamengine.client.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import webstreamengine.client.conn
import webstreamengine.client.ui.elements.UIImageButton
import webstreamengine.core.ByteUtils
import webstreamengine.core.PacketType
import webstreamengine.core.PacketUtils
import java.io.File

object TextureManager {

    private val textureMap = hashMapOf<String, Texture>()
    private val requestedIDs = mutableListOf<String>()
    private val waitingForTexture = hashMapOf<String, MutableList<Any>>()

    lateinit var blankTexture: Texture

    fun isIDInUse(id: String): Boolean {
        return textureMap.containsKey(id) || requestedIDs.contains(id)
    }

    fun loadLocal(id: String, path: String) {
        textureMap[id] = Texture(Gdx.files.internal(path))
    }

    fun applyTextureToTarget(target: Any, id: String) {
        // if we already have a texture with the given id, just pass it along
        if (textureMap.containsKey(id)) {
            applyLoadedTextureToAny(target, textureMap[id]!!)
            return
        }

        // check if the cache has a file for the given id, if so load that
        val textureFile = File(System.getProperty("user.dir"), "cache/$id.img")
        if (textureFile.exists()) {
            loadLocal(id, textureFile.absolutePath)
            applyLoadedTextureToAny(target, textureMap[id]!!)
        }

        // if we made it this far, add the given target to the waiting list
        var list = waitingForTexture[id]
        if (list == null) {
            list = mutableListOf()
            waitingForTexture[id] = list
        }
        list.add(target)

        // if the given id is not in the requested id list, send a request to the server
        if (!requestedIDs.contains(id)) {
            requestedIDs.add(id)
            conn.sendPacket(
                PacketUtils.generatePacket(
                    PacketType.REQUEST_IMAGE,
                    ByteUtils.convertStringToByteArray(id)
                )
            )
        }
    }

    fun handleTextureDelivery(id: String, bytes: ByteArray) {
        // write file bytes to a cache file
        val file = File(System.getProperty("user.dir"), "cache/$id.img")
        file.parentFile.mkdirs()
        file.writeBytes(bytes)

        // load file
        loadLocal(id, file.absolutePath)

        // remove requested id
        requestedIDs.remove(id)

        // update all targets waiting for this texture
        val texture = textureMap[id]!!
        waitingForTexture[id]?.forEach { applyLoadedTextureToAny(it, texture) }
        waitingForTexture.clear()
    }

    private fun applyLoadedTextureToAny(any: Any, texture: Texture) {
        println("Trying to apply texture to $any")
        if (any is UIImageButton)
            any.handleTextureAssign(texture)
        else
            System.err.println("No texture application methods created for target type ${any.javaClass}")
    }
}