package webstreamengine.client.entities

import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Quaternion

abstract class EntityComponent(val entity: Entity) {
    abstract fun start()
    abstract fun update()
    abstract fun render(batch: ModelBatch)
    abstract fun stop()
}
class ModelComponent(entity: Entity, private val modelInstance: ModelInstance): EntityComponent(entity) {

    init {
        entity.transformChangeCallbacks.add {
            modelInstance.transform.set(
                it.getPosition(),
                Quaternion().setEulerAngles(
                    it.getRotation().x,
                    it.getRotation().y,
                    it.getRotation().z
                ),
                it.getScale()
            )
        }
    }

    override fun start() {}
    override fun update() {}
    override fun render(batch: ModelBatch) { batch.render(modelInstance) }
    override fun stop() {}
}