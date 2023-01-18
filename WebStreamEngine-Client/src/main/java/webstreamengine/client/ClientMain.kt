package webstreamengine.client

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import webstreamengine.client.ui.UIHandler
import webstreamengine.client.application.WebStreamInfo
import webstreamengine.client.managers.InputProcessorManager
import webstreamengine.client.managers.ModelManager
import webstreamengine.client.managers.PhysicsManager
import webstreamengine.client.managers.TextureManager
import webstreamengine.client.ui.elements.UIImageButton
import webstreamengine.core.*
import java.net.Socket

lateinit var conn: Connection

val serveraddr = "localhost"
val serverport = 33215

fun main() {
    val config = Lwjgl3ApplicationConfiguration()
    config.setTitle("WebStreamEngine")
    config.setWindowedMode(1280, 720)
    Lwjgl3Application(ClientMain, config)
}

object ClientMain: ApplicationAdapter() {

    lateinit var modelbatch: ModelBatch
    lateinit var spritebatch: SpriteBatch
    lateinit var stage: Stage

    lateinit var testtexture: TextureRegionDrawable

    override fun create() {
        // create connection to server
        try {
            conn = Connection(Socket(serveraddr, serverport))
        } catch (ex: Exception) {
            System.err.println("Unable to connect to server! ${ex.message}")
        }

        // setup input
        InputProcessorManager.init()

        // setup physics
        PhysicsManager.init()

        // setup batches for rendering
        modelbatch = ModelBatch()
        spritebatch = SpriteBatch()

        // setup camera
        WebStreamInfo.initCamera()

        // initialize ui handler
        UIHandler.init()

        // send request for jar file
        conn.sendPacket(PacketUtils.generatePacket(PacketType.REQUEST_JAR, byteArrayOf()))
    }

    override fun render() {
        // handle incoming pockets
        if (conn.isDataAvailable()) ClientPacketHandler.handlePacket(conn.getDataPacket())

        // update app
        JarInterface.getApp()?.update()

        // clear screen
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        // start 3d draw
        modelbatch.begin(WebStreamInfo.cam)

        // draw entities
        WebStreamInfo.entities.forEach { it.render(modelbatch) }

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
        WebStreamInfo.entities.forEach { it.dispose() }

        // close socket
        conn.socket.close()

        // close batches
        modelbatch.dispose()
        spritebatch.dispose()

        // tell managers to dispose
        ModelManager.dispose()
    }
}