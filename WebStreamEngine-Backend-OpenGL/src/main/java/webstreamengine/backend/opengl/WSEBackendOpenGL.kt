package webstreamengine.backend.opengl

import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import webstreamengine.backend.opengl.shaders.BasicTexturedShader
import kotlin.properties.Delegates


class WSEBackendOpenGL {

    val vertices = floatArrayOf(-0.5f, -0.5f, 0f, 0.5f, -0.5f, 0f, 0f, 0.5f, 0f)
    val uvs = floatArrayOf(0f, 0f, 1f, 0f, 0.5f, 1f)
    val indices = intArrayOf(0, 1, 2)
    lateinit var testmesh: Mesh
    lateinit var shader: BasicTexturedShader

    var window by Delegates.notNull<Long>()

    fun run() {
        init()
        loop()

        // destroy the current window
        Callbacks.glfwFreeCallbacks(window)
        glfwDestroyWindow(window)

        // destroy glfw
        glfwTerminate()
        glfwSetErrorCallback(null)?.free() ?: throw NullPointerException("WARN - Error clearing GLFW error callback")
    }

    fun init() {
        // set error callback for GLFW, so it has something to print too
        GLFWErrorCallback.createPrint(System.err).set()

        // initialize GLFW, error if glfw fails
        if (!glfwInit()) throw IllegalStateException("Unable to initialize GLFW")

        // set window options
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        // create window
        window = glfwCreateWindow(640, 480, "LWJGL Backend", NULL, NULL)
        if (window == NULL) throw IllegalStateException("Unable to create GLFW window")

        // set key callback
        val keyCallback: (window: Long, key: Int, scancode: Int, action: Int, mods: Int) -> Unit = { window, key, scancode, action, mods ->
            println("Window press $window $key $scancode $action $mods")
        }
        glfwSetKeyCallback(window, keyCallback)

        // get a usable stack temporarily
        stackPush().use { stack ->
            // create pointers for width and height
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)

            // get current window set and assign the values to the previously created pointers
            glfwGetWindowSize(window, pWidth, pHeight)

            // get video mode for the primary monitor
            val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor()) ?: throw NullPointerException("Failed to get video mode")

            // set window location
            glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2)
        }

        // set the opengl context to the window
        glfwMakeContextCurrent(window)

        // set the swap interval of the window
        glfwSwapInterval(1)

        // show the window
        glfwShowWindow(window)
    }

    fun loop() {
        // dunno what this does, but it is needed before the game loop starts
        GL.createCapabilities()

        shader = BasicTexturedShader()
        testmesh = MeshLoader.createMesh(vertices, uvs, indices).addTexture("assets/cobble.jpg")

        // loop while the window is not set to close
        while (!glfwWindowShouldClose(window)) {
            // clear the screen
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

            shader.start()

            render(testmesh)

            shader.stop()

            // swap the windows buffers
            glfwSwapBuffers(window)

            // pool the glfw events
            glfwPollEvents()
        }
    }

    fun render(mesh: Mesh) {
        // bind mesh that is to be rendered
        GL30.glBindVertexArray(mesh.vao)

        // enable the first vertex array
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)

        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.texture)

        // draw the elements in the mesh
        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0)

        // unbind all
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL30.glBindVertexArray(0)
    }
}

fun main() {
    WSEBackendOpenGL().run()
}