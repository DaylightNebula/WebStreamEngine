package webstreamengine.client.networking

import org.json.JSONObject

enum class PacketType(
    val pack: (json: JSONObject) -> ByteArray,
    val unpack: (bytes: ByteReader) -> JSONObject
) {
    CHANGE_SCENE(
        { json -> ByteUtils.convertStringToByteArray(json.getString("name")) },
        { reader -> JSONObject().put("name", reader.nextString()) }
    )
}
object PacketUtils {
    fun packPacket(type: PacketType, json: JSONObject): ByteArray {
        val packed = type.pack(json)
        return byteArrayOf(
            *ByteUtils.convertIntToBytes(type.ordinal),
            *ByteUtils.convertIntToBytes(packed.size),
            *packed
        )
    }

    fun unpackPacket(type: PacketType, bytes: ByteArray): JSONObject {
        val reader = ByteReader(bytes)
        return type.unpack(reader)
    }
}