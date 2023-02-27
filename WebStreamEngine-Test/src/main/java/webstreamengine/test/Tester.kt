package webstreamengine.test

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import webstreamengine.client.application.Application
import webstreamengine.client.application.GameInfo
import webstreamengine.client.controller.ControllerSettings
import webstreamengine.client.controller.PlayerControllerComponent
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.components.ModelComponent
import webstreamengine.client.managers.SettingsElement
import webstreamengine.client.physics.ColliderComponent
import webstreamengine.client.physics.SimpleBox
import webstreamengine.client.scenes.SceneRegistry
import webstreamengine.client.ui.UserInterface

public class Tester : Application() {

    private val testentity = Entity(id = "player")

    override fun start() {
        // setup some test stuffs
        testentity.addComponent(ModelComponent(testentity, "barracks"))
        testentity.move(Vector3(0f, 0f, 0f))
        testentity.addComponent(ColliderComponent(testentity, SimpleBox(Vector3(0f, 0f, 0f), Vector3(1f, 1f, 1f)), false))
        testentity.addComponent(
            PlayerControllerComponent(testentity, ControllerSettings.genThirdPersonSettings(
                5f, Vector3(0f, 0f, 0f), 5f
            ))
        )

        // set ambient light
        GameInfo.setAmbientLight(Color.WHITE)

        UserInterface.registerInterface("test_ui") { TestUI() }
        SceneRegistry.registerScene("test_scene") { TestScene() }
        SceneRegistry.loadScene("test_scene")
    }

    override fun update() {}
    override fun stop() {}
    override fun getSettings(): Array<SettingsElement<*>> { return arrayOf() }
}