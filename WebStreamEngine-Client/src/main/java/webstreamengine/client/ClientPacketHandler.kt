package webstreamengine.client

import webstreamengine.core.ByteReader
import webstreamengine.core.PacketType

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
                val id = reader.nextString()
                val fileBytes = reader.nextByteArray()
                ModelManager.handleModelDelivery(id, fileBytes)
            }
            else -> { println("Unknown packet type $type") }
        }
    }
}