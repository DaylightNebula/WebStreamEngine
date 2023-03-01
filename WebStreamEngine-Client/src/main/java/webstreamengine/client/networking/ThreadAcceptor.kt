package webstreamengine.client.networking

import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

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
        val conn = Connection(socket)
        NetworkManager.connections.add(conn)
        runBlocking {
            withContext(Dispatchers.IO) {
                sleep(1000)
                conn.dataOut.writeFully(introPacket, 0, introPacket.size)
            }
        }
        println("Sent connection packet")
    }
}
enum class ThreadAcceptorState {
    ACCEPT_VALID,
    ACCEPT_PREVIOUS,
    DO_NOT
}