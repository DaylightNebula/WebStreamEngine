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
            else -> { println("Unknown packet type $type") }
        }
    }
}