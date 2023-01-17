package webstreamengine.test

import com.badlogic.gdx.graphics.Color
import webstreamengine.client.ModelManager
import webstreamengine.client.application.WebStreamApplication
import webstreamengine.client.application.WebStreamInfo
import webstreamengine.client.entities.Entity

public class Tester : WebStreamApplication() {
    override fun start() {
        // setup some test stuffs
        val testentity = Entity()
        ModelManager.applyModelToEntity(testentity, "barracks")
        WebStreamInfo.addEntity(testentity)

        // set ambient light
        WebStreamInfo.setAmbientLight(Color.WHITE)
    }

    override fun update() {

    }

    override fun stop() {

    }
}