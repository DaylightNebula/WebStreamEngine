package webstreamengine.server

import webstreamengine.core.ByteReader
import webstreamengine.core.PacketType

object ServerPacketHandler {
    fun handlePacket(data: ByteArray) {
        // set up a byte reader for the data
        val reader = ByteReader(data)

        // get the packet type of this packet
        val typeOrdinal = reader.nextInt()
        val type = PacketType.values()[typeOrdinal]

        // run a when operation on each possible packet type
        when (type) {
            PacketType.PING -> {}
            PacketType.REQUEST_MESH -> { println("TODO Handle Request Mesh") }
            PacketType.REQUEST_TEXTURE -> { println("TODO Handle Request Texture") }
            PacketType.DELIVER_MESH -> { println("WARN - Server should not be getting deliver mesh packet") }
            PacketType.DELIVER_TEXTURE -> { println("WARN - Server should not be getting deliver texture packet") }
        }
    }
}