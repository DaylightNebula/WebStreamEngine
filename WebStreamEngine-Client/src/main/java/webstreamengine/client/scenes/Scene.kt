package webstreamengine.client.scenes

abstract class Scene(val jarpath: String) {
    abstract fun start()
    abstract fun update()
    abstract fun stop()
}