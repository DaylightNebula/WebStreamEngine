package webstreamengine.client.networking

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import org.json.JSONObject
import webstreamengine.client.scenes.SceneRegistry
import kotlin.concurrent.thread

object NetworkManager {
    var address: String? = null
    var port: Int? = null

    var isActive = false
    var isServer = false
    val connections = mutableListOf<Connection>()
    var acceptor: ThreadAcceptor? = null
    var myID = 0

    val byteOrder = ByteOrder.LITTLE_ENDIAN

    fun connectToServer(address: String, port: Int) {
        isServer = false
        runBlocking {
            // connect to the server
            val socket = aSocket(SelectorManager(Dispatchers.IO)).tcp().connect(address, port)
            NetworkManager.isActive = true
            connections.add(Connection(0, socket))

            // get and load first scene
            println("Connected")
        }
    }

    fun becomeServer(address: String, port: Int) {
        isServer = true
        runBlocking {
            val socket = aSocket(SelectorManager(Dispatchers.IO)).tcp().bind(hostname = address, port = port)
            NetworkManager.isActive = true
            acceptor = ThreadAcceptor(
                socket,
                PacketUtils.packPacket(PacketType.CHANGE_SCENE, JSONObject().put("name", "test_scene"))
            )
            acceptor?.start()
        }
    }

    fun disconnect() {
        connections.forEach { it.close() }
        isActive = false
        isServer = false
        acceptor?.join()
    }

    fun update() {
        connections.forEach { it.update { type, bytes -> processPacket(type, bytes) } }
    }

    fun processPacket(type: PacketType, json: JSONObject) {
        // handle every packet type
        when (type) {
            PacketType.CHANGE_SCENE -> {
                // this can only be run on the client
                if (isServer) return

                // attempt to load the scene
                if (SceneRegistry.isSceneRegistered(json.getString("name")))
                    SceneRegistry.loadScene(json.getString("name"))
            }
            PacketType.SET_ID -> {
                // this can only be run on the client as the server will always have an id of 0
                if (isServer) return

                // set id
                println("Network ID = ${json.getInt("id")}")
                myID = json.getInt("id")
            }
        }
    }
}
class Connection(val id: Int, var socket: Socket, var dataIn: ByteReadChannel, var dataOut: ByteWriteChannel) {

    constructor(id: Int, socket: Socket): this(id, socket, socket.openReadChannel(), socket.openWriteChannel(autoFlush = true))

    fun update(callback: (type: PacketType, json: JSONObject) -> Unit) {
        // run on separate thread
        runBlocking {
            // while a packet is available
            while (dataIn.availableForRead >= 8) {
                // load packet
                val type = PacketType.values()[dataIn.readInt(NetworkManager.byteOrder)]
                val numBytes = dataIn.readInt(NetworkManager.byteOrder)
                val bytes = dataIn.readPacket(numBytes).readBytes()

                // call callback
                callback(type, type.unpack(ByteReader(bytes)))
            }
        }
    }

    fun close() {
        socket.close()
    }
}