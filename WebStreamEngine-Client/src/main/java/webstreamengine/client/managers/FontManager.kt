package webstreamengine.client.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import webstreamengine.client.conn
import webstreamengine.client.networkenabled
import webstreamengine.client.ui.UIElement
import webstreamengine.client.ui.microelements.TextElement
import webstreamengine.core.ByteUtils
import webstreamengine.core.PacketType
import webstreamengine.core.PacketUtils
import java.io.File

object FontManager {
    private val fontMap = hashMapOf<String, FileHandle>()
    private val requestedIDs = mutableListOf<String>()
    private val waitingForFont = hashMapOf<String, MutableList<UIElement>>()

    fun loadLocal(id: String, path: String) {
        fontMap[id] = Gdx.files.absolute(path)
    }

    fun applyFontToTarget(target: UIElement, id: String) {
        // if we already have a font with the given id, just pass it along
        if (fontMap.containsKey(id)) {
            applyLoadedFontToTarget(target, fontMap[id]!!)
            return
        }

        // if we have a cached file with the given id, load that
        val fontFile = File(System.getProperty("user.dir"), "cache/$id.ttf")
        if (fontFile.exists()) {
            loadLocal(id, fontFile.absolutePath)
            applyLoadedFontToTarget(target, fontMap[id]!!)
            return
        }

        // if we are not network enabled, return true
        if (!networkenabled) return

        // if we made it this far, add the given target to the waiting list for the given font
        var list = waitingForFont[id]
        if (list == null) {
            list = mutableListOf()
            waitingForFont[id] = list
        }
        list.add(target)

        // if the given id is not in the requested id list, send a request to the server
        if (!requestedIDs.contains(id)) {
            requestedIDs.add(id)
            conn?.sendPacket(
                PacketUtils.generatePacket(
                    PacketType.REQUEST_FONT,
                    ByteUtils.convertStringToByteArray(id)
                )
            )
        }
    }

    fun handleFontDelivery(id: String, bytes: ByteArray) {
        // write file bytes to a cache file
        val file = File(System.getProperty("user.dir"), "cache/$id.ttf")
        file.parentFile.mkdirs()
        file.writeBytes(bytes)

        // load file
        loadLocal(id, file.absolutePath)

        // remove requested id
        requestedIDs.remove(id)

        // update all targets waiting for this texture
        val font = fontMap[id]!!
        waitingForFont[id]?.forEach { applyLoadedFontToTarget(it, font) }
        waitingForFont[id]?.clear()
        waitingForFont.remove(id)
    }

    private fun applyLoadedFontToTarget(target: UIElement, handle: FileHandle) {
        if (target is TextElement) {
            target.setup(handle)
        }
    }
}