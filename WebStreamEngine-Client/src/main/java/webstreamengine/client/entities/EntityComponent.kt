package webstreamengine.client.entities

import com.badlogic.gdx.graphics.g3d.ModelBatch
import webstreamengine.client.Renderer

abstract class EntityComponent(val entity: Entity) {
    // functions that run on the server (no libgdx or renderer access)
    abstract fun serverstart()
    abstract fun serverupdate()
    abstract fun serverstop()

    // functions tha run on the client (libgdx and renderer access)
    abstract fun clientupdate()
}