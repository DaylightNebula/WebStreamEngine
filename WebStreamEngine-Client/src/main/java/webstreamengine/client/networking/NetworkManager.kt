package webstreamengine.client.networking

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*

object NetworkManager {
    var address: String? = null
    var port: Int? = null

    var isActive = false
    var isServer = false
    val connections = mutableListOf<Connection>()
    var acceptor: ThreadAcceptor? = null

    fun connectToServer(address: String, port: Int) {
        runBlocking {
            // connect to the server
            val socket = aSocket(SelectorManager(Dispatchers.IO)).tcp().connect(address, port)
            connections.add(Connection(socket))

            // get and load first scene
        }
    }

    fun becomeServer(address: String, port: Int) {
        runBlocking {
            val socket = aSocket(SelectorManager(Dispatchers.IO)).tcp().bind(hostname = address, port = port)
            val acceptor = ThreadAcceptor(socket).start()
        }
    }

    fun disconnect() {
        connections.forEach { it.close() }
        isActive = false
        isServer = false
    }
}
class Connection(var socket: Socket, var dataIn: ByteReadChannel, var dataOut: ByteWriteChannel) {

    constructor(socket: Socket): this(socket, socket.openReadChannel(), socket.openWriteChannel())

    fun close() {
        socket.close()
    }
}