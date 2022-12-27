package webstreamengine.backend.opengl

import org.joml.Vector3f
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import webstreamengine.backend.opengl.shaders.BasicTexturedShader
import webstreamengine.core.CameraInfo
import kotlin.properties.Delegates


class WSEBackendOpenGL {

    val vertices = floatArrayOf(-50f, -50f, 0f, 50f, -50f, 0f, -50f, 50f, 0f, 50f, 50f, 0f)
    val uvs = floatArrayOf(0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f)
    val indices = intArrayOf(0, 1, 2, 1, 2, 3)
    lateinit var window: OpenGLWindow

    val camera = CameraInfo(Vector3f(0f, 0f, -10000f), Vector3f(0f, 0f, 0f), 75f, .1f, 100000f)

    fun run() {
        window = OpenGLWindow("OpenGL Backend Tester", 1280, 720)
        loop()
        window.close()
    }

    fun loop() {
        val renderer = OpenGLRenderer()
        val testmesh = OpenGLMeshLoader.createMesh(vertices, uvs, indices)
        val texture = OpenGLTextureUtils.loadTexture("assets/cobble.jpg")

        // loop while the window is not set to close
        while (!window.shouldClose()) {
            renderer.startRender(window.getProjectionMatrix(camera))
            renderer.render(testmesh, texture)
            renderer.stopRender()
            window.update()
        }
    }
}

fun main() {
    WSEBackendOpenGL().run()
}