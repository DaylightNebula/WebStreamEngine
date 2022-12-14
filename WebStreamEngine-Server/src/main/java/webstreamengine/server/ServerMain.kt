package webstreamengine.server

import webstreamengine.core.Connection
import java.net.ServerSocket
import kotlin.math.absoluteValue

val port = 33215
val socket = ServerSocket(port)

val msPerTick = 10
val connections = mutableListOf<Connection>()

fun main() {
    // start acceptor to get connections while not freezing this thread
    val acceptor = ThreadAcceptor(socket) {
        println("Got connection ${it.name}")
        connections.add(it)
    }
    acceptor.start()

    ServerMeshHandler.init()

    while(true) {
        // track the start time of the tick
        val tickStartTime = System.currentTimeMillis()

        // update all connections
        updateConnections()

        // make sure each tick has the appropriate timing
        val timeToSleep = msPerTick - (System.currentTimeMillis() - tickStartTime)
        if (timeToSleep > 0)
            Thread.sleep(timeToSleep)
        else
            println("WARN - Last tick took longer then the set milliseconds per tick ($msPerTick), it took ${timeToSleep.absoluteValue} MS to long")
    }
}

fun updateConnections() {
    connections.forEach { updateConnection(it) }
    connections.removeIf { it.isClosed }
}

fun updateConnection(conn: Connection) {
    if (conn.isDataAvailable()) {
        val data = conn.getDataPacket()
        ServerPacketHandler.handlePacket(conn, data)
    }
}
