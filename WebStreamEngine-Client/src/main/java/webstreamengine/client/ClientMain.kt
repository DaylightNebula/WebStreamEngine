package webstreamengine.client

import webstreamengine.core.*
import java.lang.Exception
import java.net.Socket

lateinit var conn: Connection

val serveraddr = "localhost"
val serverport = 33215

fun main() {

    try {
        conn = Connection(Socket(serveraddr, serverport))
    } catch (ex: Exception) {
        System.err.println("Unable to connect to server! ${ex.message}")
    }

    // loop while the backend says we should not close (sleep is necessary, otherwise it runs to fast for should close)
    while (true) {
        updateSocket()

        Thread.sleep(10)
    }
}

fun updateSocket() {
    if (conn.isDataAvailable()) ClientPacketHandler.handlePacket(conn.getDataPacket())
}