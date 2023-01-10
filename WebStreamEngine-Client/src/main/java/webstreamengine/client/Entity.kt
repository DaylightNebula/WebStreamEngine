package webstreamengine.client

import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3

class Entity(
    private var position: Vector3 = Vector3(0f, 0f, 0f),
    private var rotation: Vector3 = Vector3(0f, 0f, 0f),
    private var scale: Vector3 = Vector3(1f, 1f, 1f)
) {
    private lateinit var instance: ModelInstance

    fun setModelInstance(instance: ModelInstance) {
        this.instance = instance
        updateInstanceTransform()
    }

    fun render(batch: ModelBatch) {
        if (this::instance.isInitialized)
            batch.render(instance)
    }

    fun updateInstanceTransform() {
        if (this::instance.isInitialized)
            instance.transform.set(position, Quaternion().setEulerAngles(rotation.x, rotation.y, rotation.z), scale)
    }

    fun getPosition(): Vector3 { return position }
    fun getRotation(): Vector3 { return rotation }
    fun getScale(): Vector3 { return scale }

    fun setPosition(newPosition: Vector3) {
        this.position = newPosition
        updateInstanceTransform()
    }

    fun move(move: Vector3) {
        this.position.add(move)
        updateInstanceTransform()
    }

    fun setRotation(newRotation: Vector3) {
        this.rotation = newRotation
        updateInstanceTransform()
    }

    fun rotate(rotate: Vector3) {
        this.rotation.add(rotate)
        updateInstanceTransform()
    }

    fun setScale(newScale: Vector3) {
        this.scale = newScale
        updateInstanceTransform()
    }

    fun scale(scale: Vector3) {
        this.scale.add(scale)
        updateInstanceTransform()
    }
}