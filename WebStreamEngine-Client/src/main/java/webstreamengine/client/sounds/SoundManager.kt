package webstreamengine.client.sounds

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import webstreamengine.client.networking.FuelClient
import webstreamengine.client.Renderer
import java.io.File
import kotlin.math.min

object SoundManager {

    private val soundMap = hashMapOf<String, Sound>()
    private val requestedIDs = mutableListOf<String>()
    private val waitingForSound = hashMapOf<String, MutableList<SoundRequest>>()

    private fun loadLocal(id: String, path: String) {
        soundMap[id] = Gdx.audio.newSound(Gdx.files.absolute(path))
    }

    fun submitSoundRequest(request: SoundRequest, id: String) {
        // if we already have a sound with the given id, just pass it along
        if (soundMap.containsKey(id)) {
            playSoundRequest(request, soundMap[id]!!)
            return
        }

        // if the cache has a file for the given id, load that
        if (cacheCheck(id, request, "mp3")) return
        if (cacheCheck(id, request, "wav")) return
        if (cacheCheck(id, request, "ogg")) return

        // if we made it this far, add the given sound component to the waiting list
        var list = waitingForSound[id]
        if (list == null) {
            list = mutableListOf()
            waitingForSound[id] = list
        }
        list.add(request)

        // if the given id is not the requested id list, send a request to the server
        if (!requestedIDs.contains(id)) {
            requestedIDs.add(id)
            FuelClient.requestFile(id) { handleSoundDelivery(id, it) }
        }
    }

    private fun playSoundRequest(request: SoundRequest, sound: Sound) {
        val volume = if (request.position != null) min(1f, 1f / (Renderer.cam.position.dst2(request.position))) * request.baseVolume else request.baseVolume
        sound.play(volume, request.pitch, 0f/*TODO request.pan*/)
    }

    private fun cacheCheck(id: String, request: SoundRequest, extension: String): Boolean {
        val soundFile = File(System.getProperty("user.dir"), "cache/$id.$extension")
        if (soundFile.exists()) {
            println("Found sound for id $id at ${soundFile.absolutePath}")
            loadLocal(id, soundFile.absolutePath)
            playSoundRequest(request, soundMap[id]!!)
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

    private fun handleSoundDelivery(id: String, file: File) {
        // load file
        loadLocal(id, file.absolutePath)

        // remove requested id
        requestedIDs.remove(id)

        // update all targets waiting for this texture
        val sound = soundMap[id]!!
        waitingForSound[id]?.forEach { playSoundRequest(it, sound) }
        waitingForSound[id]?.clear()
    }

    fun dispose() {
        soundMap.values.forEach { it.dispose() }
    }
}