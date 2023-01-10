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
                val modelID = reader.nextString()
                val fileBytes = FileHandler.modelFiles[modelID]
                if (fileBytes == null) {
                    println("ERROR model $modelID was not found but requested")
                    return
                }

                connection.sendPacket(
                    PacketUtils.generatePacket(
                        PacketType.DELIVER_MODEL,
                        fileBytes
                    )
                )
            }
            else -> { println("Unknown packet type $type") }
        }
    }
}