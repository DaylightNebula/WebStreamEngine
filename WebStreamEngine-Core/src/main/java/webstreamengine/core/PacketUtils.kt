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
    REQUEST_MODEL,
    DELIVER_MODEL,
    REQUEST_JAR,
    DELIVER_JAR,
    REQUEST_IMAGE,
    DELIVER_IMAGE,
    REQUEST_SOUND,
    DELIVER_MP3,
    DELIVER_WAV,
    DELIVER_OGG,
    REQUEST_FONT,
    DELIVER_FONT
}