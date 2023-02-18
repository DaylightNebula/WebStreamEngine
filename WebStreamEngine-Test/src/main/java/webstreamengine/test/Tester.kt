package webstreamengine.test

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import webstreamengine.client.application.Application
import webstreamengine.client.application.GameInfo
import webstreamengine.client.controller.ControllerSettings
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.components.SoundComponent
import webstreamengine.client.physics.SpherePhysicsComponent
import webstreamengine.client.inputs.*
import webstreamengine.client.managers.ModelManager
import webstreamengine.client.physics.PhysicsManager
import webstreamengine.client.managers.SettingsElement
import webstreamengine.client.ui.UIHandler
import webstreamengine.client.ui.elements.UIImageButton

public class Tester : Application() {

    private val testentity = Entity()
    private val soundComponent = SoundComponent(testentity)

    override fun start() {
        // setup some test stuffs
        testentity.addModelComponent("barracks")
        testentity.move(Vector3(0f, 0f, 10f))
        testentity.addComponent(SpherePhysicsComponent(testentity, Vector3(0f, 0.5f, 0f), 1f))
        testentity.addComponent(soundComponent)
        //GameInfo.addEntity(testentity)

        // set ambient light
        GameInfo.setAmbientLight(Color.WHITE)

        GameInfo.controller.changeSettings(ControllerSettings.genThirdPersonSettings(testentity, 5f, Vector3(0f, 0f, 0f), 5f))
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