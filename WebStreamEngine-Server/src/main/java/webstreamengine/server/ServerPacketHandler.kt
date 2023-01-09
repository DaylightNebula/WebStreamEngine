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
            else -> { println("Unknown packet type $type") }
        }
    }
}