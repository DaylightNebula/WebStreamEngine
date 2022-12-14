package webstreamengine.core

object PacketUtils {
    fun generatePacket(packetType: PacketType, packetData: ByteArray): ByteArray {
        return byteArrayOf(
            *ByteUtils.convertIntToBytes(packetData.size + 4),
            *ByteUtils.convertIntToBytes(packetType.ordinal),
            *packetData
        )
    }

    fun convertIntToPacketType(packetID: Int): PacketType {
        return PacketType.values()[packetID]
    }
}
enum class PacketType {
    PING,
    REQUEST_MESH,
    REQUEST_TEXTURE,
    DELIVER_MESH,
    DELIVER_TEXTURE
}