package webstreamengine.client

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Vector3
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
import java.lang.Thread.sleep
import java.net.Socket

const val MS_PER_TICK = 16
val programArgs = hashMapOf<String, String>()
var headless = false
var conn: Connection? = null
var running = true

fun main(args: Array<String>) {
    processProgramArgs(args)
    println("Starting with args ${args.map { it }}")

    if (programArgs["headless"]?.equals("true", ignoreCase = true) == true)
        headless = true

    FuelClient.startClient(args)

    // start client main
    ClientMain.start()

    // update loop, locked to ms per tick
    while (running) {
        // update the client
        val start = System.currentTimeMillis()
        ClientMain.update()

        // sleep long enough to sync with ms per tick
        val sleepMS = MS_PER_TICK - (System.currentTimeMillis() - start)
        if (sleepMS > 0)
            sleep(sleepMS)
    }

    // stop the client
    ClientMain.dispose()
}

object ClientMain {

    fun start() {
        // setup settings
        SettingsManager.init(
            File(
                System.getProperty("user.dir"),
                "gamedata/settings.json"
            )
        )

        // setup camera
        GameInfo.initCamera()

        // create renderer
        if (!headless)
            RendererStart().start()

        JarInterface.init()
    }

    fun update() {
        // update the settings manager
        SettingsManager.update()

        // update game info
        GameInfo.update()

        // update app
        JarInterface.getApp()?.update()
        SceneRegistry.updateScene()

        // update input
        InputManager.update()

        // update entities
        EntityChunks.updateEntities(Vector3.Zero)
    }

    fun dispose() {
        // stop app
        JarInterface.getApp()?.stop()

        // dispose of all entities
        EntityChunks.clear()

        // close socket
        conn?.socket?.close()

        // tell managers to dispose
        SoundManager.dispose()
    }
}

fun processProgramArgs(inputArgs: Array<String>) {
    inputArgs.forEach {
        if (!it.startsWith("-")) return@forEach
        val tokens = it.split("=", limit = 2)
        programArgs[tokens.first().substring(1)] = tokens.last()
    }
}