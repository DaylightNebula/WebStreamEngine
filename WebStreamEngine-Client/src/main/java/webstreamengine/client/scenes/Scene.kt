package webstreamengine.client.scenes

import org.json.JSONObject
import webstreamengine.client.networking.Connection
import webstreamengine.client.networking.NetworkManager
import webstreamengine.client.networking.PacketType
import webstreamengine.client.networking.PacketUtils

abstract class Scene(val jarpath: String) {
    abstract fun generalStart()
    abstract fun serverUpdate()
    abstract fun clientUpdate()
    abstract fun generalStop()
    abstract fun netJoin(conn: Connection)
    abstract fun netDisconnect(conn: Connection)
    abstract fun netCommunicate(json: JSONObject)

    fun sendCommunication(json: JSONObject) {
        if (NetworkManager.isActive) {
            val packet = PacketUtils.packPacket(PacketType.SCENE_COMMUNICATE, json)
            NetworkManager.connections.forEach { it.sendRaw(packet) }
        }
    }
}