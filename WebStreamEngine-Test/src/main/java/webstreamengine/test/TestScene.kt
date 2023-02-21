package webstreamengine.test

import webstreamengine.client.scenes.Scene

class TestScene: Scene("test_scene") {
    override fun start() {
        println("Start")
    }

    override fun update() {
        println("Update")
    }

    override fun stop() {
        println("Stop")
    }
}