package webstreamengine.client

import webstreamengine.backend.opengl.OpenGLRenderBackend
import org.joml.Vector3f
import webstreamengine.core.*
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.net.Socket

lateinit var conn: Connection
lateinit var backend: RenderBackend

val serveraddr = "localhost"
val serverport = 33215

//val vertices = floatArrayOf(-50f, -50f, 0f, 50f, -50f, 0f, -50f, 50f, 0f, 50f, 50f, 0f)
//val uvs = floatArrayOf(0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f)
//val indices = intArrayOf(0, 1, 2, 1, 2, 3)

fun main() {
    // build info for the current render backend
    val backendInfo = RenderBackendInfo(
        "Web Stream Engine",
        Pair(1280, 720)
    )

    // create a backend to use
    backend = OpenGLRenderBackend(backendInfo)

    // start the backend
    backend.start()

    try {
        conn = Connection(Socket(serveraddr, serverport))
    } catch (ex: Exception) {
        System.err.println("Unable to connect to server! ${ex.message}")
    }

    // wait until the backend is done loading
    while (!backend.isLoadingComplete()) { Thread.sleep(100) }

    // create a basic entity
    val entityDesc = EntityDescriptor(
        Vector3f(0f, 0f, 0f),
        Vector3f(0f, 0f, 0f),
        Vector3f(1f, 1f, 1f),
        null
    )

    // add entity to backend
    backend.addOrUpdateEntityDescriptor(0, entityDesc)

    ClientMeshHandler.applyMeshToEntity(entityDesc, conn, "barracks")

    val camera = CameraInfo(
        Vector3f(0f, 0f, 4f),
        Vector3f(0f, 0f, 0f),
        75f, .1f, 100000f
    )

    backend.updateCameraInfo(camera)

    // loop while the backend says we should not close (sleep is necessary, otherwise it runs to fast for should close)
    while (!backend.shouldClose()) {
        updateSocket()

        Thread.sleep(10)
    }

    // close the backend
    backend.close()
}

fun updateSocket() {
    if (conn.isDataAvailable()) ClientPacketHandler.handlePacket(backend, conn.getDataPacket())
}