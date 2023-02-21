package webstreamengine.client.entities

import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import org.json.JSONObject
import webstreamengine.client.JarInterface
import webstreamengine.client.application.GameInfo
import webstreamengine.client.managers.ModelManager
import webstreamengine.client.physics.SimpleBox
import kotlin.math.pow

class Entity(
    var id: String,
    private var position: Vector3 = Vector3(0f, 0f, 0f),
    private var rotation: Vector3 = Vector3(0f, 0f, 0f),
    private var scale: Vector3 = Vector3(1f, 1f, 1f),
    private var registerAutomatically: Boolean = true,
    val global: Boolean = false,
    val keep: Boolean = false
) {
    private val components = mutableListOf<EntityComponent>()
    val transformChangeCallbacks = mutableListOf<(entity: Entity) -> Unit>()
    var box = SimpleBox(Vector3(), Vector3())
    val chunks = mutableListOf<Chunk>()

    constructor(
        path: String,
        position: Vector3 = Vector3(0f, 0f, 0f),
        rotation: Vector3 = Vector3(0f, 0f, 0f),
        scale: Vector3 = Vector3(1f, 1f, 1f),
    ): this(
        JSONObject(JarInterface.getTextResource("entities/$path.json")),
        position, rotation, scale
    )

    constructor(
        json: JSONObject,
        position: Vector3 = Vector3(0f, 0f, 0f),
        rotation: Vector3 = Vector3(0f, 0f, 0f),
        scale: Vector3 = Vector3(1f, 1f, 1f),
    ): this(
        json.optString("id") ?: throw IllegalArgumentException("Entity is required to have a id in json object"),
        position, rotation, scale,
        json.optBoolean("registerAutomatically", true),
        json.optBoolean("global", false),
        json.optBoolean("keep", false)
    ) {
        if (json.has("components"))
            components.addAll(
                json.getJSONArray("components")
                    .mapNotNull { EntityComponentRegistry.createComponentViaJSON(this, it as JSONObject) }
            )
    }

/*
    constructor(
        modelKey: String,
        position: Vector3 = Vector3(0f, 0f, 0f),
        rotation: Vector3 = Vector3(0f, 0f, 0f),
        scale: Vector3 = Vector3(1f, 1f, 1f),
        registerAutomatically: Boolean = true,
        global: Boolean = false
    ): this(position, rotation, scale, registerAutomatically, global) {
        addModelComponent(modelKey)
    }
*/

    init {
        if (registerAutomatically) EntityChunks.addEntity(this)
    }

    fun addModelComponent(modelID: String) {
        ModelManager.applyModelToEntity(this, modelID)
    }

    fun generateTransformationMatrix(offset: Vector3 = Vector3(0f, 0f, 0f)): Matrix4 {
        val matrix = Matrix4()
        matrix.idt()
        matrix.scale(scale.x, scale.y, scale.z)
        matrix.rotate(Quaternion().setEulerAngles(rotation.x, rotation.y, rotation.z))
        matrix.translate(position.x + offset.x, position.y + offset.y, position.z + offset.z)
        return matrix
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

    private var canRender = false
    fun update() {
        components.forEach { it.update() }
        canRender = true
    }

    fun render(batch: ModelBatch) {
        // make sure we only render once per frame
        if (canRender) {
            components.forEach { it.render(batch) }
            canRender = false
        }
    }

    fun dispose() {
        components.forEach { it.stop() }
    }

    private fun updateInstanceTransform() {
        transformChangeCallbacks.forEach { it(this) }
        EntityChunks.updateEntity(this)
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

    inline fun <reified T: EntityComponent> getComponentOfType(): T? {
        return getComponents().firstOrNull { it is T } as? T
    }
}