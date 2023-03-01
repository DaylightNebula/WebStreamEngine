package webstreamengine.client.entities

abstract class EntityComponent(val entity: Entity) {
    // functions that are always called regardless of start or stop
    abstract fun generalStart()
    abstract fun generalStop()

    // functions that run on the server (no libgdx or renderer access)
    abstract fun serverUpdate()

    // functions tha run on the client (libgdx and renderer access)
    abstract fun clientUpdate()
}