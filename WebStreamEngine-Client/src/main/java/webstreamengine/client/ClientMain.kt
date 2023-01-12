package webstreamengine.client

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import webstreamengine.client.entities.Entity
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

    lateinit var cam: PerspectiveCamera
    lateinit var modelbatch: ModelBatch

    val entities = mutableListOf<Entity>()

    override fun create() {
        // create connection to server
        try {
            conn = Connection(Socket(serveraddr, serverport))
        } catch (ex: Exception) {
            System.err.println("Unable to connect to server! ${ex.message}")
        }

        // setup model batch for rendering
        modelbatch = ModelBatch()

        // setup camera
        cam = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        cam.position.set(10f, 10f, 10f)
        cam.lookAt(0f, 0f, 0f)
        cam.near = .1f
        cam.far = 1000f
        cam.update()

        // setup some test stuffs
        val testentity = Entity()
        ModelManager.applyModelToEntity(testentity, "barracks")
        addEntity(testentity)
    }

    fun addEntity(entity: Entity) {
        entities.add(entity)
    }

    fun removeEntity(entity: Entity) {
        entities.remove(entity)
    }

    override fun render() {
        // handle incoming pockets
        if (conn.isDataAvailable()) ClientPacketHandler.handlePacket(conn.getDataPacket())

        // clear screen
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        // start 3d draw
        modelbatch.begin(cam)

        // draw entities
        entities.forEach { it.render(modelbatch) }

        // end 3d draw
        modelbatch.end()
    }

    override fun dispose() {
        entities.forEach { it.dispose() }

        // close socket
        conn.socket.close()

        // close batches
        modelbatch.dispose()

        // tell managers to dispose
        ModelManager.dispose()
    }
}