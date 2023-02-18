package webstreamengine.client

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
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
import webstreamengine.client.physics.PhysicsManager
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

        // setup physics
        PhysicsManager.init()

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
    }

    override fun render() {
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