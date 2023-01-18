package webstreamengine.test

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import webstreamengine.client.ModelManager
import webstreamengine.client.application.WebStreamApplication
import webstreamengine.client.application.WebStreamInfo
import webstreamengine.client.entities.Entity

public class Tester : WebStreamApplication() {
    override fun start() {
        // setup some test stuffs
        val testentity = Entity()
        ModelManager.applyModelToEntity(testentity, "barracks")
        testentity.move(Vector3(0f, 0f, 10f))
        WebStreamInfo.addEntity(testentity)

        // set ambient light
        WebStreamInfo.setAmbientLight(Color.WHITE)
    }

    override fun update() {

    }

    override fun stop() {

    }
}