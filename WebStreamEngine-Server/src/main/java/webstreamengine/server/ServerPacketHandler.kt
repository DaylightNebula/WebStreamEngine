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
        val type = PacketType.values()[typeOrdinal]

        // run a when operation on each possible packet type
        when (type) {
            PacketType.PING -> { }
            PacketType.REQUEST_MESH -> {
                val meshID = reader.nextString()
                val bytes = ServerMeshHandler.requestMesh(meshID)

                if (bytes == null) {
                    println("WARN - Client ${connection.name} asked for a mesh with ID $meshID that did not exist")
                    return
                }

                connection.sendPacket(PacketUtils.generatePacket(PacketType.DELIVER_MESH, bytes))
            }
            PacketType.REQUEST_TEXTURE -> { println("TODO Handle Request Texture") }
            PacketType.DELIVER_MESH -> { println("WARN - Server should not be getting deliver mesh packet") }
            PacketType.DELIVER_TEXTURE -> { println("WARN - Server should not be getting deliver texture packet") }
        }
    }
}