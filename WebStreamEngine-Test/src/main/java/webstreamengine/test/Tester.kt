package webstreamengine.test

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import webstreamengine.client.application.Application
import webstreamengine.client.application.GameInfo
import webstreamengine.client.controller.ControllerSettings
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.components.SoundComponent
import webstreamengine.client.managers.SettingsElement
import webstreamengine.client.physics.ColliderComponent
import webstreamengine.client.physics.SimpleBox
import webstreamengine.client.ui.UIManager
import webstreamengine.client.ui.UserInterface

public class Tester : Application() {

    private val testentity = Entity(id = "player")
    private val soundComponent = SoundComponent(testentity)

    override fun start() {
        // setup some test stuffs
        testentity.addModelComponent("barracks")
        testentity.move(Vector3(0f, 0f, 0f))
        testentity.addComponent(soundComponent)
        testentity.addComponent(ColliderComponent(testentity, SimpleBox(Vector3(0f, 0f, 0f), Vector3(1f, 1f, 1f)), false))

        // set ambient light
        GameInfo.setAmbientLight(Color.WHITE)

        GameInfo.controller.changeSettings(ControllerSettings.genThirdPersonSettings(testentity, 5f, Vector3(0f, 0f, 0f), 5f))

        UserInterface.registerInterface("test_ui") { TestUI() }
        UserInterface.loadInterface("test_ui")

        Entity("test_entity", position = Vector3(0f, 0f, -10f))
    }

    override fun update() {
//        if (InputManager.getElement("click")?.getValue() == true) {
//            val pickRay = GameInfo.cam.getPickRay(InputManager.mouseX.toFloat(), InputManager.mouseY.toFloat())
//            val hit = PhysicsManager.rayCast(pickRay)
//            println("Ray Cast hit $hit")
//
//            soundComponent.playSound("test_sound")
//        }
    }

    override fun stop() {

    }

    override fun getSettings(): Array<SettingsElement<*>> { return arrayOf() }

}