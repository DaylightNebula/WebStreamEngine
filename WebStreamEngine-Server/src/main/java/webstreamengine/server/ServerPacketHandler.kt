package webstreamengine.server

import webstreamengine.core.ByteReader
import webstreamengine.core.Connection
import webstreamengine.core.PacketType
import webstreamengine.core.PacketUtils

object ServerPacketHandler {
    fun handlePacket(connection: Connection, data: ByteArray) {
        // set up a byte reader for the data
        val reader = ByteReader(data)

        // get the packet type of this packet
        val typeOrdinal = reader.nextInt()

        // run a when operation on each possible packet type
        when (val type = PacketType.values()[typeOrdinal]) {
            PacketType.PING -> { }
            PacketType.REQUEST_MODEL -> {
                // get model ID
                val modelID = reader.nextString()

                // try to get file bytes from the file handler by the given model ID
                val fileBytes = FileHandler.modelFiles[modelID]
                if (fileBytes == null) {
                    println("ERROR model $modelID was not found but requested")
                    return
                }

                // send back a model delivery packet
                connection.sendPacket(
                    PacketUtils.generatePacket(
                        PacketType.DELIVER_MODEL,
                        fileBytes
                    )
                )
            }
            PacketType.REQUEST_JAR -> {
                // send back a jar delivery packet
                connection.sendPacket(
                    PacketUtils.generatePacket(
                        PacketType.DELIVER_JAR,
                        FileHandler.jarFile
                    )
                )
            }
            PacketType.REQUEST_IMAGE -> {
                // get image ID
                val imageID = reader.nextString()

                // try to get an image file byte by the id from the file handler
                val fileBytes = FileHandler.imageFiles[imageID]
                if (fileBytes == null) {
                    println("ERROR image $imageID was not found but requested")
                    return
                }

                // send back a image delivery packet
                connection.sendPacket(
                    PacketUtils.generatePacket(
                        PacketType.DELIVER_IMAGE,
                        fileBytes
                    )
                )
            }
            else -> { println("Unknown packet type $type") }
        }
    }
}