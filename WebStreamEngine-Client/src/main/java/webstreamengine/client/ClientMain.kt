package webstreamengine.client

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Vector3
import webstreamengine.client.ui.UIHandler
import webstreamengine.client.application.GameInfo
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.EntityChunks
import webstreamengine.client.inputs.InputManager
import webstreamengine.client.managers.*
import webstreamengine.client.physics.ColliderComponent
import webstreamengine.client.physics.FakeRayCastPlane
import webstreamengine.client.physics.PhysicsController
import webstreamengine.client.physics.SimpleBox
import webstreamengine.core.*
import java.io.File
import java.lang.IllegalArgumentException
import java.net.Socket

var conn: Connection? = null

var networkenabled = false
var programargs = arrayOf<String>()

fun main(args: Array<String>) {
    println("Starting with args ${args.map { it }}")

    networkenabled = args.size >= 2
    programargs = args

    val config = Lwjgl3ApplicationConfiguration()
    config.setTitle("WebStreamEngine")
    config.setWindowedMode(1280, 720)
    Lwjgl3Application(ClientMain, config)
}

object ClientMain: ApplicationAdapter() {

    private lateinit var modelbatch: ModelBatch
    private lateinit var spritebatch: SpriteBatch

    private lateinit var testRed: Entity
    private lateinit var testBlue: Entity
    private lateinit var testGreen: Entity

    override fun create() {
        // setup settings
        SettingsManager.init(
            File(
                System.getProperty("user.dir"),
                "gamedata/settings.json"
            )
        )

        // setup input
        InputProcessorManager.init()
        UIHandler.init()
        InputManager.init(
            File(
                System.getProperty("user.dir"),
                "gamedata/current_input.json"
            )
        )

        // setup batches for rendering
        modelbatch = ModelBatch()
        spritebatch = SpriteBatch()

        // setup camera
        GameInfo.initCamera()

        // create connection to server
        if (networkenabled) {
            try {
                conn = Connection(Socket(programargs[0], programargs[1].toInt()))
                conn!!.sendPacket(PacketUtils.generatePacket(PacketType.REQUEST_JAR, byteArrayOf()))
            } catch (ex: Exception) {
                System.err.println("Unable to connect to server! ${ex.message}")
            }
        } else {
            // load jar file if we can find it
            val file = File("cache/").listFiles()?.firstOrNull { it.extension == "jar" } ?: throw IllegalArgumentException("Could not find a jar file in the cache.")
            if (file.exists()) JarInterface.init(file)
        }

        // todo remove
        ModelManager.createTestBox("test_blue", Vector3(1f, 1f, 1f), Color.BLUE)
        ModelManager.createTestBox("test_red", Vector3(1f, 1f, 1f), Color.RED)
        ModelManager.createTestBox("test_green", Vector3(10f, 0.1f, 10f), Color.GREEN)
        testBlue = Entity("test_blue", Vector3(-4f, 2f, 0f))
        testRed = Entity("test_red", Vector3(4f, 2f, 0f))
        testGreen = Entity("test_green", Vector3(0f, 0f, 0f))
        testBlue.addComponent(ColliderComponent(testBlue, SimpleBox(Vector3.Zero, Vector3(1f, 1f, 1f)), true))
        testRed.addComponent(ColliderComponent(testRed, SimpleBox(Vector3.Zero, Vector3(1f, 1f, 1f)), false))
        testGreen.addComponent(ColliderComponent(testGreen, SimpleBox(Vector3.Zero, Vector3(10f, 0.1f, 10f)), false))
    }

    override fun render() {
        // todo remove
        if (InputManager.isMouseButtonUp(Input.Buttons.LEFT)) {
            val ray = GameInfo.cam.getPickRay(InputManager.mouseX.toFloat(), InputManager.mouseY.toFloat())
            val result = PhysicsController.rayCast(ray)
            if (result != null) testRed.setPosition(result.second/*Vector3(result.z, result.y, result.x)*/)
            println("Result $result direction ${ray.direction}")
        }

        // handle incoming pockets
        if (networkenabled && conn!!.isDataAvailable()) ClientPacketHandler.handlePacket(conn!!.getDataPacket())

        // update the settings manager
        SettingsManager.update()

        // update game info
        GameInfo.update()

        // update model manager
        ModelManager.update()

        // update app
        JarInterface.getApp()?.update()

        // update input
        InputManager.update()

        // update entities
        EntityChunks.updateEntities(GameInfo.cam.position)

        // clear screen
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        // start 3d draw
        modelbatch.begin(GameInfo.cam)

        // draw entities
        EntityChunks.renderEntities(modelbatch, GameInfo.cam.position)

        // end 3d draw
        modelbatch.end()

        // draw test ui
        spritebatch.begin()
        UIHandler.renderUI()
        spritebatch.end()
    }

    override fun dispose() {
        // stop app
        JarInterface.getApp()?.stop()

        // dispose UI
        UIHandler.dispose()

        // dispose of all entities
        EntityChunks.disposeAll()

        // close socket
        conn?.socket?.close()

        // close batches
        modelbatch.dispose()
        spritebatch.dispose()

        // tell managers to dispose
        ModelManager.dispose()
        TextureManager.dispose()
        SoundManager.dispose()
    }
}