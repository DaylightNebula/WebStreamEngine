package webstreamengine.test

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import webstreamengine.client.application.Application
import webstreamengine.client.application.GameInfo
import webstreamengine.client.controller.ControllerSettings
import webstreamengine.client.controller.PlayerControllerComponent
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.components.ModelComponent
import webstreamengine.client.headless
import webstreamengine.client.managers.SettingsElement
import webstreamengine.client.networking.NetworkManager
import webstreamengine.client.physics.ColliderComponent
import webstreamengine.client.physics.SimpleBox
import webstreamengine.client.scenes.SceneRegistry
import webstreamengine.client.ui.UserInterface

public class Tester : Application() {

    private lateinit var testentity: Entity

    override fun start() {
        // setup some test stuffs
        Entity.createFromPath("player") { entity ->
            testentity = entity
        }

        // set ambient light
        GameInfo.setAmbientLight(Color.WHITE)

        // load first scene
        UserInterface.registerInterface("test_ui") { TestUI() }
        SceneRegistry.registerScene("test_scene") { TestScene() }
//        SceneRegistry.loadScene("test_scene")

        // do connection
        if (headless)
            NetworkManager.becomeServer("localhost", 9003)
        else
            NetworkManager.connectToServer("localhost", 9003)
    }

    override fun update() {}
    override fun stop() {}
    override fun getSettings(): Array<SettingsElement<*>> { return arrayOf() }
}