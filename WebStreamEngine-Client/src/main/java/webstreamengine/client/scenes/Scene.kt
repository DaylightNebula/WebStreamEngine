package webstreamengine.client.scenes

abstract class Scene(val jarpath: String) {
    abstract fun generalStart()
    abstract fun serverUpdate()
    abstract fun clientUpdate()
    abstract fun generalStop()
}