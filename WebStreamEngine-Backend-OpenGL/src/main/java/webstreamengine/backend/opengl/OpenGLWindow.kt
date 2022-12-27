package webstreamengine.backend.opengl

import org.joml.Matrix4f
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWWindowSizeCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import webstreamengine.core.CameraInfo

class OpenGLWindow(var name: String, var width: Int, var height: Int) {
    private var window = -1L
    private var aspectRatio = width.toFloat() / height.toFloat()

    init {
        // set error callback for GLFW, so it has something to print too
        GLFWErrorCallback.createPrint(System.err).set()

        // initialize GLFW, error if glfw fails
        if (!GLFW.glfwInit()) throw IllegalStateException("Unable to initialize GLFW")

        // set window options
        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE)

        // create window
        window = GLFW.glfwCreateWindow(width, height, name, MemoryUtil.NULL, MemoryUtil.NULL)
        if (window == MemoryUtil.NULL) throw IllegalStateException("Unable to create GLFW window")

        // set key callback
        val keyCallback: (window: Long, key: Int, scancode: Int, action: Int, mods: Int) -> Unit = { window, key, scancode, action, mods ->
            println("Window press $window $key $scancode $action $mods")
        }
        GLFW.glfwSetKeyCallback(window, keyCallback)

        // get a usable stack temporarily
        MemoryStack.stackPush().use { stack ->
            // create pointers for width and height
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)

            // get current window set and assign the values to the previously created pointers
            GLFW.glfwGetWindowSize(window, pWidth, pHeight)

            // get video mode for the primary monitor
            val vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()) ?: throw NullPointerException("Failed to get video mode")

            // set window location
            GLFW.glfwSetWindowPos(
                window,
                (vidmode.width() - pWidth.get(0)) / 2,
                (vidmode.height() - pHeight.get(0)) / 2
            )
        }

        // set the opengl context to the window
        GLFW.glfwMakeContextCurrent(window)

        // set the swap interval of the window
        GLFW.glfwSwapInterval(1)

        // show the window
        GLFW.glfwShowWindow(window)

        // create resize callback
        val me = this
        GLFW.glfwSetWindowSizeCallback(window, object : GLFWWindowSizeCallback() {
            override fun invoke(window: Long, width: Int, height: Int) {
                me.resize(width, height)
            }
        })

        // add capabilities
        GL.createCapabilities()
    }

    fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
        this.aspectRatio = width.toFloat() / height.toFloat()
        GL11.glViewport(0, 0, width, height)
    }

    fun getProjectionMatrix(camera: CameraInfo): Matrix4f {
        //return Matrix4f().ortho2D(width / -2f, width / 2f, height / -2f, height / 2f)
        return Matrix4f().perspective(camera.fov, aspectRatio, camera.near, camera.far)
    }

    fun update() {
        GLFW.glfwSwapBuffers(window)
        GLFW.glfwPollEvents()
    }

    fun shouldClose(): Boolean {
        if (window == -1L) return false
        return GLFW.glfwWindowShouldClose(window)
    }

    fun close() {
        Callbacks.glfwFreeCallbacks(window)
        GLFW.glfwDestroyWindow(window)

        GLFW.glfwTerminate()
        GLFW.glfwSetErrorCallback(null)?.free() ?: throw NullPointerException("Could not set error callback on window close")
    }
}