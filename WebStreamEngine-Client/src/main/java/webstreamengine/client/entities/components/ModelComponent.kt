package webstreamengine.client.entities.components

import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import webstreamengine.client.application.GameInfo
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.EntityComponent
import webstreamengine.client.physics.SimpleBox

class ModelComponent(entity: Entity, private val modelInstance: ModelInstance): EntityComponent(entity) {

    init {
        // call and register update transform function
        entity.transformChangeCallbacks.add { updateTransform() }
        updateTransform()

        // update bounds
        val bounds = modelInstance.model.calculateBoundingBox(BoundingBox())
        entity.box = SimpleBox(bounds.getCenter(Vector3()), Vector3(bounds.width, bounds.height, bounds.depth))
    }

    private fun updateTransform() {
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