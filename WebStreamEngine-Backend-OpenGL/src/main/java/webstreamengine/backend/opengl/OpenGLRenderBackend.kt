package webstreamengine.backend.opengl

import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import webstreamengine.backend.opengl.shaders.BasicTexturedShader
import webstreamengine.core.*

class OpenGLRenderBackend(info: RenderBackendInfo): RenderBackend(info) {

    private val entityDescriptors = hashMapOf<Int, EntityDescriptor>()

    // mesh stuffs
    private val meshMap = hashMapOf<Int, OpenGLMesh?>()
    private val meshLoadQueue = mutableListOf<Pair<Int, MeshInfo>>() // Format, Mesh id to info

    // texture stuffs
    private val textureMap = hashMapOf<Int, Int?>()
    private val textureLoadQueue = mutableListOf<Pair<Int, String>>()

    // loading stuffs
    private var running = true
    private var doneLoading = false

    // rendering stuffs
    private var window: Long = -1L
    private lateinit var shader: BasicTexturedShader
    private var cameraInfo = CameraInfo(Vector3f(0f, 0f, 10f), Vector3f(0f, 0f, 0f))
    private lateinit var viewMatrix: Matrix4f

    override fun run() {
        super.run()

        // set error callback for GLFW, so it has something to print too
        GLFWErrorCallback.createPrint(System.err).set()

        // initialize GLFW, error if glfw fails
        if (!GLFW.glfwInit()) throw IllegalStateException("Unable to initialize GLFW")

        // set window options
        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE)

        // create window
        window = GLFW.glfwCreateWindow(info.winDimensions.first, info.winDimensions.second, info.winName, MemoryUtil.NULL, MemoryUtil.NULL)
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

        // create opengl capabilities on this thread
        GL.createCapabilities()

        // we are done loading
        doneLoading = true

        // create shader
        shader = BasicTexturedShader()

        //shader.setProjectionMatrix(MathUtils.generatePerspectiveMatrix(info.winDimensions, 75f, .1f, 100f))

        while(running) {
            // run final load on mesh
            if (meshLoadQueue.size > 0) {
                meshLoadQueue.forEach { finalLoadMesh(it.first, it.second) }
                meshLoadQueue.clear()
            }

            // run final load on textures
            if (textureLoadQueue.size > 0) {
                textureLoadQueue.forEach { finalLoadTexture(it.first, it.second) }
                textureLoadQueue.clear()
            }
            shader.setProjectionMatrix(MatrixUtils.getOrthoProjection(info.winDimensions))

            // clear the old frame
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

            // start the current shader
            shader.start()

            entityDescriptors.forEach { id, desc ->
                render(desc)
            }

            // stop the current shader
            shader.stop()

            // swap the window buffers
            GLFW.glfwSwapBuffers(window)

            // poll all events (like input and such)
            GLFW.glfwPollEvents()
        }
    }

    fun render(descriptor: EntityDescriptor) {
        if (descriptor.mesh == null || descriptor.texture == null) return
        val mesh = meshMap[descriptor.mesh] ?: return
        val texture = textureMap[descriptor.texture] ?: return

        //shader.setViewMatrix(MathUtils.generateViewMatrix(descriptor.position, descriptor.rotation, descriptor.scale, cameraInfo))

        // bind mesh that is to be rendered
        GL30.glBindVertexArray(mesh.vao)

        // enable the first vertex array
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)

        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture)

        // draw the elements in the mesh
        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0)

        // unbind all
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL30.glBindVertexArray(0)
    }

    override fun close() {
        // force the main loop to stop
        running = false
    }

    override fun loadMesh(info: MeshInfo): Int {
        // get the id for the mesh
        val id = meshMap.size + meshLoadQueue.size

        // save slot in the mesh map
        meshMap[id] = null

        // save info to the load queue
        meshLoadQueue.add(Pair(id, info))

        // return the id
        return id
    }

    fun finalLoadMesh(id: Int, info: MeshInfo) {
        meshMap[id] = OpenGLMeshLoader.createMesh(info.vertices, info.uvs, info.indices)
    }

    override fun loadLocalTexture(path: String): Int {
        // get the id for the texture
        val id = textureMap.size + textureLoadQueue.size

        // save slot for the texture in the texture map
        textureMap[id] = null

        // save the texture path to the load queue
        textureLoadQueue.add(Pair(id, path))

        // return the id
        return id
    }

    fun finalLoadTexture(id: Int, path: String) {
        textureMap[id] = OpenGLTextureUtils.loadTexture(path)
    }

    override fun addOrUpdateEntityDescriptor(id: Int, descriptor: EntityDescriptor) {
        entityDescriptors[id] = descriptor
    }

    override fun clearCurrentEntities() {
        entityDescriptors.clear()
    }

    override fun shouldClose(): Boolean {
        // if window has not been initialized, return false
        if (window == -1L) return false

        // check if the windows state says we should close
        return GLFW.glfwWindowShouldClose(window)
    }

    override fun isLoadingComplete(): Boolean {
        return doneLoading
    }

    override fun updateCameraInfo(info: CameraInfo) {
        cameraInfo = info
        //viewMatrix = MathUtils.generateViewMatrix(info.position, info.rotation)
    }
}