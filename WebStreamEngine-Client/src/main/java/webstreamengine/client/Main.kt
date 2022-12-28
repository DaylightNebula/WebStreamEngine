package webstreamengine.client

import webstreamengine.backend.opengl.OpenGLRenderBackend
import webstreamengine.core.EntityDescriptor
import webstreamengine.core.MeshInfo
import webstreamengine.core.RenderBackendInfo
import org.joml.Vector3f
import webstreamengine.core.CameraInfo
import java.lang.Exception
import java.net.Socket

lateinit var socket: Socket

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
    val backend = OpenGLRenderBackend(backendInfo)

    // start the backend
    backend.start()

    try {
        socket = Socket(serveraddr, serverport)
    } catch (ex: Exception) {
        System.err.println("Unable to connect to server! ${ex.message}")
    }

    // wait until the backend is done loading
    while (!backend.isLoadingComplete()) { Thread.sleep(1) }

    // load mesh
//    val mesh = backend.loadMesh(MeshInfo(vertices, uvs, indices))
//
//    // load texture
//    val texture = backend.loadLocalTexture("assets/cobble.jpg")
//
//    // create a basic entity
//    val entityDesc = EntityDescriptor(
//        Vector3f(0f, 0f, -200f),
//        Vector3f(0f, 0f, 0f),
//        Vector3f(1f, 1f, 1f),
//        mesh, texture
//    )
//
//    // add entity to backend
//    backend.addOrUpdateEntityDescriptor(0, entityDesc)

    val camera = CameraInfo(
        Vector3f(0f, 0f, 0f),
        Vector3f(0f, 0f, 0f),
        75f, .1f, 100000f
    )

    backend.updateCameraInfo(camera)

    // loop while the backend says we should not close (sleep is necessary, otherwise it runs to fast for should close)
    while (!backend.shouldClose()) { Thread.sleep(1) }

    // close the backend
    backend.close()
}