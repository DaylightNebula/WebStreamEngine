package webstreamengine.core

import webstreamengine.core.ByteUtils
import webstreamengine.core.PacketType
import webstreamengine.core.PacketUtils
import java.io.DataOutputStream
import java.io.ObjectOutputStream
import java.net.Socket
import java.net.SocketException

class Connection(val socket: Socket) {
    val name = socket.inetAddress.toString()
    val inputstream = socket.getInputStream()
    val outputstream = socket.getOutputStream()

    var lastTestTime = System.currentTimeMillis()
    var isClosed = false
    val testIntervalMS = 1000

    fun isDataAvailable(): Boolean {
        // every test interval, try to send a test ping to the server, if it errors out, the connection has been closed
        if (System.currentTimeMillis() - lastTestTime > testIntervalMS) {
            lastTestTime = System.currentTimeMillis()
            try {
                sendPacket(PacketUtils.generatePacket(PacketType.PING, byteArrayOf()))
            } catch (ex: SocketException) {
                isClosed = true
            }
        }

        // return if any data is available
        return inputstream.available() > 0
    }

    fun getDataPacket(): ByteArray {
        // read the packet size
        val packetSize = ByteUtils.convertBytesToInt(inputstream.readNBytes(4), 0)

        // return packet
        return inputstream.readNBytes(packetSize)
    }

    fun sendPacket(bytes: ByteArray) {
        println("Trying to send packet of size ${bytes.size}")
        outputstream.write(bytes)
    }
}