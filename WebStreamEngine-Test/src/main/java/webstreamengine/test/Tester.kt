package webstreamengine.test

import webstreamengine.client.ModelManager
import webstreamengine.client.application.WebStreamApplication
import webstreamengine.client.application.WebStreamInfo
import webstreamengine.client.entities.Entity

object Tester : WebStreamApplication() {
    override fun start() {
        // setup some test stuffs
        val testentity = Entity()
        ModelManager.applyModelToEntity(testentity, "barracks")
        WebStreamInfo.addEntity(testentity)
    }

    override fun update() {

    }

    override fun stop() {

    }
}