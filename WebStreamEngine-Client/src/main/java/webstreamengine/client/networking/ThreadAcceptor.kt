package webstreamengine.client.networking

import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import webstreamengine.client.entities.EntityHandler
import webstreamengine.client.scenes.SceneRegistry

class ThreadAcceptor(private val serverSocket: ServerSocket, private val introPacket: ByteArray): Thread() {

    private var state = ThreadAcceptorState.ACCEPT_VALID

    override fun run() {
        while (NetworkManager.isActive) {
            runBlocking {
                val socket = serverSocket.accept()

                if (state == ThreadAcceptorState.DO_NOT) return@runBlocking
                if (state == ThreadAcceptorState.ACCEPT_PREVIOUS) throw NotImplementedError()

                if (state == ThreadAcceptorState.ACCEPT_VALID) {
                    newConnection(socket)
                }
            }
        }
    }

    fun newConnection(socket: Socket) {
        val id = NetworkManager.connections.size + 1
        val conn = Connection(id, socket)
        NetworkManager.connections.add(conn)
        runBlocking {
            withContext(Dispatchers.IO) {
                conn.sendPacket(PacketType.SET_ID, JSONObject().put("id", id))
                conn.dataOut.writeFully(introPacket, 0, introPacket.size)
                EntityHandler.entities.forEach { entity ->
                    if (!entity.sceneCreated) {
                        val packet = entity.getCreatePacket()
                        conn.dataOut.writeFully(packet, 0, packet.size)
                    }
                }
                SceneRegistry.handleNetJoin(conn)
            }
        }
    }
}
enum class ThreadAcceptorState {
    ACCEPT_VALID,
    ACCEPT_PREVIOUS,
    DO_NOT
}