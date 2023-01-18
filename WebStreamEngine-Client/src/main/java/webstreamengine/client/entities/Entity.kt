package webstreamengine.client.entities

import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Vector3
import webstreamengine.client.managers.ModelManager

class Entity(
    private var position: Vector3 = Vector3(0f, 0f, 0f),
    private var rotation: Vector3 = Vector3(0f, 0f, 0f),
    private var scale: Vector3 = Vector3(1f, 1f, 1f)
) {
    private val components = mutableListOf<EntityComponent>()
    val transformChangeCallbacks = mutableListOf<(entity: Entity) -> Unit>()

    fun addModelComponent(modelID: String) {
        ModelManager.applyModelToEntity(this, modelID)
    }

    fun setModelInstance() {
        updateInstanceTransform()
    }

    fun addComponent(component: EntityComponent) {
        components.add(component)
        component.start()
    }

    fun getComponents(): List<EntityComponent> {
        return components
    }

    fun removeComponent(component: EntityComponent) {
        components.remove(component)
        component.stop()
    }

    fun update() {
        components.forEach { it.update() }
    }

    fun render(batch: ModelBatch) {
        components.forEach { it.render(batch) }
    }

    fun dispose() {
        components.forEach { it.stop() }
    }

    fun updateInstanceTransform() {
        transformChangeCallbacks.forEach { it(this) }
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