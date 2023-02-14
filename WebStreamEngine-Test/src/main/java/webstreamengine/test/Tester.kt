package webstreamengine.test

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import webstreamengine.client.application.Application
import webstreamengine.client.application.GameInfo
import webstreamengine.client.controller.ControllerSettings
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.components.SoundComponent
import webstreamengine.client.entities.components.SpherePhysicsComponent
import webstreamengine.client.inputs.InputManager
import webstreamengine.client.managers.ModelManager
import webstreamengine.client.managers.PhysicsManager
import webstreamengine.client.ui.UIHandler
import webstreamengine.client.ui.elements.UIImageButton

public class Tester : Application() {

    val testentity = Entity()
    val test2 = Entity()
    val soundComponent = SoundComponent(testentity)

    override fun start() {
        ModelManager.createTestBox("test", Vector3(1f, 1f, 1f), Color.RED)
        test2.addModelComponent("test")
        test2.setPosition(Vector3(0f, 0f, 0f))
        GameInfo.addEntity(test2)

        // setup some test stuffs
        testentity.addModelComponent("barracks")
        testentity.move(Vector3(0f, 0f, 10f))
        testentity.addComponent(SpherePhysicsComponent(testentity, Vector3(0f, 0.5f, 0f), 1f))
        testentity.addComponent(soundComponent)
        GameInfo.addEntity(testentity)

        // test ui
        val testbutton = UIImageButton(
            "play_button",
            0.1f, 0.1f,
            0.27f, 0.27f
        ) {
            println("Button click $it")
        }
        UIHandler.addUIElement(testbutton)

        // set ambient light
        GameInfo.setAmbientLight(Color.WHITE)

        GameInfo.controller.changeSettings(ControllerSettings.genThirdPersonSettings(testentity, 5f, Vector3(0f, 0f, 0f), 5f))
    }

    var ticker = 0f
    override fun update() {
        // test ray cast thing
        if (InputManager.isMouseButtonUp(Input.Buttons.LEFT)) {
            val pickRay = GameInfo.cam.getPickRay(InputManager.mouseX.toFloat(), InputManager.mouseY.toFloat())
            val hit = PhysicsManager.rayCast(pickRay)
            println("Ray Cast hit $hit")

            soundComponent.playSound("test_sound")
        }
    }

    override fun stop() {

    }
}