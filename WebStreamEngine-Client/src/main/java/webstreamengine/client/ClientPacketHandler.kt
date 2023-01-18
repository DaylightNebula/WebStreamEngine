package webstreamengine.client

import webstreamengine.client.managers.ModelManager
import webstreamengine.client.managers.TextureManager
import webstreamengine.core.ByteReader
import webstreamengine.core.PacketType
import java.io.File

object ClientPacketHandler {
    fun handlePacket(data: ByteArray) {
        // set up a byte reader for the data
        val reader = ByteReader(data)

        // get the packet type of this packet
        val typeOrdinal = reader.nextInt()

        // run a when operation on each possible packet type
        when (val type = PacketType.values()[typeOrdinal]) {
            PacketType.PING -> { }
            PacketType.DELIVER_MODEL -> {
                // get model id
                val id = reader.nextString()

                // get image bytes
                val fileBytes = reader.nextByteArray()

                // call handle delivery of the texture
                ModelManager.handleModelDelivery(id, fileBytes)
            }
            PacketType.DELIVER_JAR -> {
                // get file bytes
                val fileBytes = reader.nextByteArray()

                // get target file
                val file = File(System.getProperty("user.dir"), "cache\\jar.jar")

                // write file bytes
                file.writeBytes(fileBytes)

                // initialize jar interface with this file
                JarInterface.init(file)
            }
            PacketType.DELIVER_IMAGE -> {
                // get image id
                val id = reader.nextString()

                // get image bytes
                val bytes = reader.nextByteArray()

                // call handle delivery of the texture
                TextureManager.handleTextureDelivery(id, bytes)
            }
            else -> { println("Unknown packet type $type") }
        }
    }
}