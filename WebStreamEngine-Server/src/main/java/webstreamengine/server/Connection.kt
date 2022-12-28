package webstreamengine.server

import webstreamengine.core.ByteUtils
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
            try {
                outputstream.write(byteArrayOf(*ByteUtils.convertIntToBytes(0), *ByteUtils.convertIntToBytes(0)))
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

        //
        return inputstream.readNBytes(packetSize)
    }
}