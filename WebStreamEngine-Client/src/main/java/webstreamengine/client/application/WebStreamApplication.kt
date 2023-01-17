package webstreamengine.client.application

abstract class WebStreamApplication {
    abstract fun start()
    abstract fun update()
    abstract fun stop()
}