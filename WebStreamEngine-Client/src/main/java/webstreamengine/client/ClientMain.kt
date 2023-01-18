package webstreamengine.client

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Vector3
import webstreamengine.client.application.WebStreamInfo
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.components.DirectionalLightComponent
import webstreamengine.client.entities.components.PointLightComponent
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

    override fun create() {
        // create connection to server
        try {
            conn = Connection(Socket(serveraddr, serverport))
        } catch (ex: Exception) {
            System.err.println("Unable to connect to server! ${ex.message}")
        }

        WebStreamInfo.initCamera()

        // setup model batch for rendering
        modelbatch = ModelBatch()

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
    }

    override fun dispose() {
        // stop app
        JarInterface.getApp()?.stop()

        // dispose of all entities
        WebStreamInfo.entities.forEach { it.dispose() }

        // close socket
        conn.socket.close()

        // close batches
        modelbatch.dispose()

        // tell managers to dispose
        ModelManager.dispose()
    }
}