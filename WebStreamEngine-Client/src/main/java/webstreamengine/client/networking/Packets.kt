package webstreamengine.client.networking

import org.json.JSONArray
import org.json.JSONObject

enum class PacketType(
    val pack: (json: JSONObject) -> ByteArray,
    val unpack: (bytes: ByteReader) -> JSONObject
) {
    CHANGE_SCENE(
        { json -> ByteUtils.convertStringToByteArray(json.getString("name")) },
        { reader -> JSONObject().put("name", reader.nextString()) }
    ),
    SET_ID(
        { json -> ByteUtils.convertIntToBytes(json.getInt("id")) },
        { reader -> JSONObject().put("id", reader.nextInt()) }
    ),
    ASSIGN_CONTROL(
        { json ->
            byteArrayOf(
                *ByteUtils.convertIntToBytes(json.getInt("net_id")),
                *ByteUtils.convertStringToByteArray(json.getString("entity_id"))
            )
        },
        { reader ->
            JSONObject()
                .put("net_id", reader.nextInt())
                .put("entity_id", reader.nextString())
        }
    ),
    CREATE_ENTITY_FROM_SCRIPT(
        { json ->
            val position = json.optJSONArray("position") ?: JSONArray().put(0f).put(0f).put(0f)
            val rotation = json.optJSONArray("rotation") ?: org.json.JSONArray().put(0f).put(0f).put(0f)
            val scale = json.optJSONArray("scale") ?: org.json.JSONArray().put(1f).put(1f).put(1f)
            byteArrayOf(
                *ByteUtils.convertStringToByteArray(json.getString("id")),
                *ByteUtils.convertStringToByteArray(json.getString("path")),
                *ByteUtils.convertFloatToByteArray(position.getFloat(0)),
                *ByteUtils.convertFloatToByteArray(position.getFloat(1)),
                *ByteUtils.convertFloatToByteArray(position.getFloat(2)),
                *ByteUtils.convertFloatToByteArray(rotation.getFloat(0)),
                *ByteUtils.convertFloatToByteArray(rotation.getFloat(1)),
                *ByteUtils.convertFloatToByteArray(rotation.getFloat(2)),
                *ByteUtils.convertFloatToByteArray(scale.getFloat(0)),
                *ByteUtils.convertFloatToByteArray(scale.getFloat(1)),
                *ByteUtils.convertFloatToByteArray(scale.getFloat(2)),
            )
        },
        { reader ->
            JSONObject()
                .put("id", reader.nextString())
                .put("path", reader.nextString())
                .put("position", JSONArray().put(reader.nextFloat()).put(reader.nextFloat()).put(reader.nextFloat()))
                .put("rotation", JSONArray().put(reader.nextFloat()).put(reader.nextFloat()).put(reader.nextFloat()))
                .put("scale", JSONArray().put(reader.nextFloat()).put(reader.nextFloat()).put(reader.nextFloat()))
        }
    ),
    UPDATE_ENTITY_TRANSFORM(
        { json ->
            val position = json.optJSONArray("position") ?: org.json.JSONArray().put(0f).put(0f).put(0f)
            val rotation = json.optJSONArray("rotation") ?: org.json.JSONArray().put(0f).put(0f).put(0f)
            val scale = json.optJSONArray("scale") ?: org.json.JSONArray().put(1f).put(1f).put(1f)
            byteArrayOf(
                *ByteUtils.convertStringToByteArray(json.getString("id")),
                *ByteUtils.convertFloatToByteArray(position.getFloat(0)),
                *ByteUtils.convertFloatToByteArray(position.getFloat(1)),
                *ByteUtils.convertFloatToByteArray(position.getFloat(2)),
                *ByteUtils.convertFloatToByteArray(rotation.getFloat(0)),
                *ByteUtils.convertFloatToByteArray(rotation.getFloat(1)),
                *ByteUtils.convertFloatToByteArray(rotation.getFloat(2)),
                *ByteUtils.convertFloatToByteArray(scale.getFloat(0)),
                *ByteUtils.convertFloatToByteArray(scale.getFloat(1)),
                *ByteUtils.convertFloatToByteArray(scale.getFloat(2)),
            )
        },
        { reader ->
            org.json.JSONObject()
                .put("id", reader.nextString())
                .put("position", org.json.JSONArray().put(reader.nextFloat()).put(reader.nextFloat()).put(reader.nextFloat()))
                .put("rotation", org.json.JSONArray().put(reader.nextFloat()).put(reader.nextFloat()).put(reader.nextFloat()))
                .put("scale", org.json.JSONArray().put(reader.nextFloat()).put(reader.nextFloat()).put(reader.nextFloat()))
        }
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