package webstreamengine.client

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.AnimationController
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import webstreamengine.client.application.GameInfo
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.EntityHandler
import webstreamengine.client.entities.components.ModelComponent
import webstreamengine.client.inputs.InputManager
import webstreamengine.client.managers.InputProcessorManager
import webstreamengine.client.managers.ModelManager
import webstreamengine.client.managers.TextureManager
import webstreamengine.client.networking.NetworkManager
import webstreamengine.client.physics.ColliderComponent
import webstreamengine.client.physics.PhysicsController
import webstreamengine.client.physics.SimpleBox
import webstreamengine.client.scenes.SceneRegistry
import webstreamengine.client.ui.UIManager
import java.io.File

class RendererStart: Thread() {
    override fun run() {
        val config = Lwjgl3ApplicationConfiguration()
        config.setTitle("WebStreamEngine")
        config.setWindowedMode(1280, 720)
        Lwjgl3Application(Renderer, config)
    }
}
object Renderer: ApplicationAdapter() {

    lateinit var cam: PerspectiveCamera
    private lateinit var modelbatch: ModelBatch
    private lateinit var spritebatch: SpriteBatch

    var currentSeconds = 0f
    var deltaTime = 0f

    override fun create() {
        // create basic camera
        cam = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        cam.position.set(0f, 0f, 0f)
        cam.lookAt(0f, 0f, 10f)
        cam.near = .1f
        cam.far = 1000f
        cam.update()

        // setup input
        InputProcessorManager.init()
        InputManager.init(
            File(
                System.getProperty("user.dir"),
                "gamedata/current_input.json"
            )
        )

        // setup batches for rendering
        modelbatch = ModelBatch()
        spritebatch = SpriteBatch()
    }

    override fun render() {
        deltaTime = Gdx.graphics.deltaTime
        currentSeconds += Gdx.graphics.deltaTime

        // update visual handlers
        ModelManager.update()
        UIManager.update()

        // update physics
        PhysicsController.update()

        // update scene registry if needed
        if (!NetworkManager.isActive || !NetworkManager.isServer)
            SceneRegistry.clientUpdate()

        // clear screen
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        // start 3d draw
        modelbatch.begin(cam)

        // draw entities
        EntityHandler.renderEntities()

        // end 3d draw
        modelbatch.end()

        // draw test ui
        spritebatch.begin()
        UIManager.render(spritebatch)
        spritebatch.end()
    }

    override fun dispose() {
        ModelManager.dispose()
        TextureManager.dispose()

        // close batches
        modelbatch.dispose()
        spritebatch.dispose()
        running = false
    }

    fun renderComponent(component: ModelComponent) {
        if (!component.hasInstance()) {
            val model = ModelManager.getModelByKey(component.key)
            if (model == null) {
                println("Model $model")
                return
            }
            component.instance = ModelInstance(model).apply {
                this.transform.scl(component.entity.getScale())
                this.transform.rotate(
                    Quaternion().setEulerAngles(
                        component.entity.getRotation().x,
                        component.entity.getRotation().y,
                        component.entity.getRotation().z
                    )
                )
                this.transform.translate(component.entity.getPosition())
            }
            component.animController = AnimationController(component.instance)
        }
        modelbatch.render(component.instance, GameInfo.environment)
    }

    fun renderImage(texture: Texture, position: Vector2, size: Vector2) {
        spritebatch.draw(texture, position.x, position.y, size.x, size.y)
    }

    override fun resize(width: Int, height: Int) { UIManager.isDirty = true }

    fun isCameraReady(): Boolean = this::cam.isInitialized

    fun getMousePosition(): Vector2 = Vector2(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
    fun getWindowSize(): Vector2 = Vector2(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    fun getGdxFile(absolutePath: String): FileHandle = Gdx.files.absolute(absolutePath)
    fun setCursorCatched(bool: Boolean) {
        Gdx.input.isCursorCatched = bool
    }
}