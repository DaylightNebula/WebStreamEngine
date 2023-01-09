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
    lateinit var window: OpenGLWindow
    lateinit var renderer: OpenGLRenderer
    private var cameraInfo = CameraInfo(Vector3f(0f, 0f, 0f), Vector3f(0f, 0f, 0f), 75f, .1f, 100f)

    override fun run() {
        super.run()

        window = OpenGLWindow(info.winName, info.winDimensions.first, info.winDimensions.second)
        renderer = OpenGLRenderer()

        // we are done loading
        doneLoading = true

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

            renderer.startRender(window.getProjectionMatrix(cameraInfo))

            entityDescriptors.forEach { id, desc ->
                if (desc.mesh != null)
                    renderer.render(desc, cameraInfo, meshMap[desc.mesh!!] ?: return@forEach)
            }

            renderer.stopRender()

            window.update()
        }

        window.close()
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
        return window.shouldClose()
    }

    override fun isLoadingComplete(): Boolean {
        return doneLoading
    }

    override fun updateCameraInfo(info: CameraInfo) {
        cameraInfo = info
        //viewMatrix = MathUtils.generateViewMatrix(info.position, info.rotation)
    }
}