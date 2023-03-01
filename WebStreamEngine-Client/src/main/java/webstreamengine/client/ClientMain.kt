package webstreamengine.client

import webstreamengine.client.application.GameInfo
import webstreamengine.client.entities.EntityHandler
import webstreamengine.client.inputs.InputManager
import webstreamengine.client.managers.*
import webstreamengine.client.networking.FuelClient
import webstreamengine.client.networking.NetworkManager
import webstreamengine.client.scenes.SceneRegistry
import webstreamengine.client.sounds.SoundManager
import java.io.File
import java.lang.Thread.sleep

const val MS_PER_TICK = 16
val programArgs = hashMapOf<String, String>()
var headless = false
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

        // update app
        JarInterface.getApp()?.update()

        // update server
        if (!NetworkManager.isActive || NetworkManager.isServer)
            SceneRegistry.serverUpdate()

        // update input
        InputManager.update()

        // update entities
        if (!NetworkManager.isActive || NetworkManager.isServer)
            EntityHandler.serverUpdate()

        // update the network
        NetworkManager.update()
    }

    fun dispose() {
        // stop app
        JarInterface.getApp()?.stop()

        // dispose of all entities
        EntityHandler.clear()

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