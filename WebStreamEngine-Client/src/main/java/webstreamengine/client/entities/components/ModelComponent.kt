package webstreamengine.client.entities.components

import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.AnimationController
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import webstreamengine.client.Renderer
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.EntityComponent
import webstreamengine.client.managers.ModelManager
import webstreamengine.client.physics.SimpleBox

class ModelComponent(entity: Entity, val key: String): EntityComponent(entity) {

    internal lateinit var instance: ModelInstance
    internal lateinit var animController: AnimationController

    init {
        // call and register update transform function
        entity.transformChangeCallbacks.add { updateTransform() }
        updateTransform()

        // update bounds
        entity.box = SimpleBox(Vector3(), Vector3(1f, 1f, 1f))

        // tell model manager to get the key
        ModelManager.requestIfNecessary(key, true) {}
    }

    private fun updateTransform() {
        if (!hasInstance()) return
        instance.apply {
            this.transform = Matrix4()
            this.transform.scl(entity.getScale())
            this.transform.rotate(Quaternion().setEulerAngles(entity.getRotation().x, entity.getRotation().y, entity.getRotation().z))
            this.transform.translate(entity.getPosition())
        }
    }

    fun getAnimController(): AnimationController? {
        if (!this::animController.isInitialized) return null
        return animController
    }

    override fun generalStart() {}
    override fun serverUpdate() {}
    override fun clientUpdate() { Renderer.renderComponent(this) }
    override fun generalStop() {}

    fun hasInstance(): Boolean = this::instance.isInitialized
}