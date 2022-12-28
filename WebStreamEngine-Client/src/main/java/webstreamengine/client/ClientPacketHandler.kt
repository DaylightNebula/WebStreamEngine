package webstreamengine.client

import webstreamengine.core.ByteReader
import webstreamengine.core.PacketType
import webstreamengine.core.RenderBackend

object ClientPacketHandler {
    fun handlePacket(backend: RenderBackend, data: ByteArray) {
        // set up a byte reader for the data
        val reader = ByteReader(data)

        // get the packet type of this packet
        val typeOrdinal = reader.nextInt()
        val type = PacketType.values()[typeOrdinal]

        // run a when operation on each possible packet type
        when (type) {
            PacketType.PING -> { }
            PacketType.REQUEST_MESH -> { println("WARN - Client should not be getting request mesh packet") }
            PacketType.REQUEST_TEXTURE -> { println("WARN - Client should not be getting request texture packet") }
            PacketType.DELIVER_MESH -> { ClientMeshHandler.handleMeshDelivery(backend, reader) }
            PacketType.DELIVER_TEXTURE -> { println("TODO Handle Deliver Texture") }
        }
    }
}