package webstreamengine.client.scenes

import webstreamengine.client.networking.Connection

abstract class Scene(val jarpath: String) {
    abstract fun generalStart()
    abstract fun serverUpdate()
    abstract fun clientUpdate()
    abstract fun generalStop()
    abstract fun netJoin(conn: Connection)
    abstract fun netDisconnect(conn: Connection)
}