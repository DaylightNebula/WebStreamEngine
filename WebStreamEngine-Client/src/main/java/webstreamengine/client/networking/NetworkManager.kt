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

    val byteOrder = ByteOrder.LITTLE_ENDIAN

    fun connectToServer(address: String, port: Int) {
        isServer = false
        runBlocking {
            // connect to the server
            val socket = aSocket(SelectorManager(Dispatchers.IO)).tcp().connect(address, port)
            NetworkManager.isActive = true
            connections.add(Connection(socket))

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
                println("Loading ${json.toString(1)}, registered? ${SceneRegistry.isSceneRegistered(json.getString("name"))}")
                if (SceneRegistry.isSceneRegistered(json.getString("name")))
                    SceneRegistry.loadScene(json.getString("name"))
            }
        }
    }
}
class Connection(var socket: Socket, var dataIn: ByteReadChannel, var dataOut: ByteWriteChannel) {

    constructor(socket: Socket): this(socket, socket.openReadChannel(), socket.openWriteChannel(autoFlush = true))

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