package webstreamengine.client.entities

import com.badlogic.gdx.graphics.g3d.ModelBatch

abstract class EntityComponent(val entity: Entity) {
    abstract fun start()
    abstract fun update()
    abstract fun render(batch: ModelBatch)
    abstract fun stop()
}