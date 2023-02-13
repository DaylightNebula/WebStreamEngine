package webstreamengine.client.entities.components

import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Quaternion
import webstreamengine.client.application.GameInfo
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.EntityComponent

class ModelComponent(entity: Entity, private val modelInstance: ModelInstance): EntityComponent(entity) {

    init {
        entity.transformChangeCallbacks.add { updateTransform() }
        updateTransform()
    }

    fun updateTransform() {
        modelInstance.transform.set(
            entity.getPosition(),
            Quaternion().setEulerAngles(
                entity.getRotation().x,
                entity.getRotation().y,
                entity.getRotation().z
            ),
            entity.getScale()
        )
    }

    override fun start() {}
    override fun update() {}
    override fun render(batch: ModelBatch) { batch.render(modelInstance, GameInfo.environment) }
    override fun stop() {}
}