package webstreamengine.client

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.ModelBatch
import webstreamengine.client.application.GameInfo
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.EntityChunks
import webstreamengine.client.inputs.InputManager
import webstreamengine.client.managers.*
import webstreamengine.client.scenes.SceneRegistry
import webstreamengine.client.sounds.SoundManager
import webstreamengine.client.ui.UIManager
import webstreamengine.core.*
import java.io.File
import java.lang.IllegalArgumentException
import java.net.Socket

var conn: Connection? = null

fun main(args: Array<String>) {
    println("Starting with args ${args.map { it }}")

    FuelClient.startClient(args)

    val config = Lwjgl3ApplicationConfiguration()
    config.setTitle("WebStreamEngine")
    config.setWindowedMode(1280, 720)
    Lwjgl3Application(ClientMain, config)
}

object ClientMain: ApplicationAdapter() {

    private lateinit var modelbatch: ModelBatch
    private lateinit var spritebatch: SpriteBatch

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

        JarInterface.init()
    }

    override fun render() {
        // update the settings manager
        SettingsManager.update()

        // update game info
        GameInfo.update()

        // update model manager
        ModelManager.update()

        // update app
        JarInterface.getApp()?.update()
        SceneRegistry.updateScene()

        // update input
        InputManager.update()

        UIManager.update()

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
        UIManager.render(spritebatch)
        spritebatch.end()
    }

    override fun dispose() {
        // stop app
        JarInterface.getApp()?.stop()

        // dispose UI
        UIManager.dispose()

        // dispose of all entities
        EntityChunks.clear()

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

    override fun resize(width: Int, height: Int) { UIManager.isDirty = true }
}