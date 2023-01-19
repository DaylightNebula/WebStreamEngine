package webstreamengine.client.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import webstreamengine.client.conn
import webstreamengine.client.entities.components.SoundComponent
import webstreamengine.core.ByteUtils
import webstreamengine.core.PacketType
import webstreamengine.core.PacketUtils
import java.io.File

object SoundManager {

    private val soundMap = hashMapOf<String, Sound>()
    private val requestedIDs = mutableListOf<String>()
    private val waitingForSound = hashMapOf<String, MutableList<SoundComponent>>()

    fun loadLocal(id: String, path: String) {
        soundMap[id] = Gdx.audio.newSound(Gdx.files.absolute(path))
    }

    fun applySoundToComponent(player: SoundComponent, id: String) {
        // if we already have a sound with the given id, just pass it along
        if (soundMap.containsKey(id)) {
            player.handleSound(id, soundMap[id]!!)
            return
        }

        // if the cache has a file for the given id, load that
        if (cacheCheck(id, player, "mp3")) return
        if (cacheCheck(id, player, "wav")) return
        if (cacheCheck(id, player, "ogg")) return

        // if we made it this far, add the given sound component to the waiting list
        var list = waitingForSound[id]
        if (list == null) {
            list = mutableListOf()
            waitingForSound[id] = list
        }
        list.add(player)

        // if the given id is not the requested id list, send a request to the server
        if (!requestedIDs.contains(id)) {
            requestedIDs.add(id)
            conn.sendPacket(
                PacketUtils.generatePacket(
                    PacketType.REQUEST_SOUND,
                    ByteUtils.convertStringToByteArray(id)
                )
            )
            println("Requested sound $id")
        }
    }

    fun cacheCheck(id: String, player: SoundComponent, extension: String): Boolean {
        val soundFile = File(System.getProperty("user.dir"), "cache/$id.$extension")
        if (soundFile.exists()) {
            loadLocal(id, soundFile.absolutePath)
            player.handleSound(id, soundMap[id]!!)
            return true
        }
        return false
    }

    fun handleMP3Delivery(id: String, bytes: ByteArray) {
        // write file bytes to a cache file
        val file = File(System.getProperty("user.dir"), "cache/$id.mp3")
        file.parentFile.mkdirs()
        file.writeBytes(bytes)

        // handle final sound delivery
        handleSoundDelivery(id, file)
    }

    fun handleWavDelivery(id: String, bytes: ByteArray) {
        // write file bytes to a cache file
        val file = File(System.getProperty("user.dir"), "cache/$id.wav")
        file.parentFile.mkdirs()
        file.writeBytes(bytes)

        // handle final sound delivery
        handleSoundDelivery(id, file)
    }

    fun handleOggDelivery(id: String, bytes: ByteArray) {
        // write file bytes to a cache file
        val file = File(System.getProperty("user.dir"), "cache/$id.ogg")
        file.parentFile.mkdirs()
        file.writeBytes(bytes)

        // handle final sound delivery
        handleSoundDelivery(id, file)
    }

    fun handleSoundDelivery(id: String, file: File) {
        // load file
        loadLocal(id, file.absolutePath)

        // remove requested id
        requestedIDs.remove(id)

        // update all targets waiting for this texture
        val sound = soundMap[id]!!
        waitingForSound[id]?.forEach { it.handleSound(id, sound) }
        waitingForSound[id]?.clear()
    }

    fun dispose() {
        soundMap.values.forEach { it.dispose() }
    }
}