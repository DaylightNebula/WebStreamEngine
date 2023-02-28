package webstreamengine.client.networking

import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import webstreamengine.client.networking.Connection

class ThreadAcceptor(val serverSocket: ServerSocket): Thread() {

    var state = ThreadAcceptorState.ACCEPT_VALID

    override fun run() {
        while (NetworkManager.isActive) {
            runBlocking {
                val socket = serverSocket.accept()

                if (state == ThreadAcceptorState.DO_NOT) return@runBlocking
                if (state == ThreadAcceptorState.ACCEPT_PREVIOUS) throw NotImplementedError()

                if (state == ThreadAcceptorState.ACCEPT_VALID) {
                    NetworkManager.connections.add(
                        Connection(
                            socket,
                        )
                    )
                }
                println("Got connection ${socket.localAddress}")
            }
        }
    }
}
enum class ThreadAcceptorState {
    ACCEPT_VALID,
    ACCEPT_PREVIOUS,
    DO_NOT
}