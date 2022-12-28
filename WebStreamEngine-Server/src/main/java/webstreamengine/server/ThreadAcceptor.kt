package webstreamengine.server

import java.net.ServerSocket

class ThreadAcceptor(val server: ServerSocket, val connectCallback: (conn: Connection) -> Unit): Thread() {
    override fun run() {
        while (true) {
            val socket = server.accept()
            connectCallback(Connection(socket))
        }
    }
}