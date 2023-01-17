package webstreamengine.client.entities.components

import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Quaternion
import webstreamengine.client.application.WebStreamInfo
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.EntityComponent

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
    override fun render(batch: ModelBatch) { batch.render(modelInstance, WebStreamInfo.environment) }
    override fun stop() {}
}