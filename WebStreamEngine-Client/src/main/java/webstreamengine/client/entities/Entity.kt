package webstreamengine.client.entities

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import org.json.JSONArray
import org.json.JSONObject
import webstreamengine.client.networking.FuelClient
import webstreamengine.client.networking.NetworkManager
import webstreamengine.client.networking.PacketType
import webstreamengine.client.networking.PacketUtils
import webstreamengine.client.physics.SimpleBox
import java.util.*

class Entity(
    val id: UUID = UUID.randomUUID(),
    val sceneCreated: Boolean,
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
    var path = ""

    var assignedTo = 0
        set(value) {
            // if we are an active server, send assign packet
            if (NetworkManager.isActive && NetworkManager.isServer) {
                NetworkManager.connections.filter { it.id == value }.forEach { conn ->
                    conn.sendPacket(
                        PacketType.ASSIGN_CONTROL,
                        JSONObject()
                            .put("net_id", value)
                            .put("entity_id", id.toString())
                    )
                }
            }

            // set value
            println("Assigned entity $id to NET_ID = $value")
            field = value
        }

    companion object {
        fun createFromPath(
            id: UUID = UUID.randomUUID(),
            sceneCreated: Boolean,
            path: String,
            position: Vector3 = Vector3(0f, 0f, 0f),
            rotation: Vector3 = Vector3(0f, 0f, 0f),
            scale: Vector3 = Vector3(1f, 1f, 1f),
            createCallback: (entity: Entity) -> Unit
        ) {
            // if we are a server, broadcast create
            if (NetworkManager.isActive && NetworkManager.isServer) {
                val packet = PacketUtils.packPacket(
                    PacketType.CREATE_ENTITY_FROM_SCRIPT,
                    JSONObject()
                        .put("id", id.toString())
                        .put("path", path)
                        .put("position", position.toJSONArray())
                        .put("rotation", rotation.toJSONArray())
                        .put("scale", scale.toJSONArray())
                )
                NetworkManager.connections.forEach { it.sendRaw(packet) }
            }

            // request file and create entity
            FuelClient.requestFile("$path.entity") {
                val entity = Entity(
                    id, sceneCreated,
                    JSONObject(it.readText()),
                    position, rotation, scale
                )
                entity.path = path
                createCallback(entity)
            }
        }
    }

    constructor(
        id: UUID = UUID.randomUUID(),
        sceneCreated: Boolean,
        json: JSONObject,
        position: Vector3 = Vector3(0f, 0f, 0f),
        rotation: Vector3 = Vector3(0f, 0f, 0f),
        scale: Vector3 = Vector3(1f, 1f, 1f),
    ): this(
        id, sceneCreated,
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

    init {
        if (registerAutomatically) EntityHandler.addEntity(this)
    }

    fun getCreatePacket(): ByteArray {
        return PacketUtils.packPacket(
            PacketType.CREATE_ENTITY_FROM_SCRIPT,
            JSONObject()
                .put("id", id.toString())
                .put("path", path)
                .put("position", position.toJSONArray())
                .put("rotation", rotation.toJSONArray())
                .put("scale", scale.toJSONArray())
        )
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
        component.generalStart()
    }

    fun getComponents(): List<EntityComponent> {
        return components
    }

    fun removeComponent(component: EntityComponent) {
        components.remove(component)
        component.generalStop()
    }

    fun serverUpdate() {
        components.forEach { it.serverUpdate() }
    }

    fun clientUpdate() {
        // make sure we only render once per frame
        components.forEach { it.clientUpdate() }
    }

    fun dispose() {
        components.forEach { it.generalStop() }
    }

    private fun updateInstanceTransform(silent: Boolean = false) {
        transformChangeCallbacks.forEach { it(this) }
        EntityHandler.updateEntity(this)

        if (!silent) {
            val packet = PacketUtils.packPacket(PacketType.UPDATE_ENTITY_TRANSFORM, JSONObject().put("id", id.toString()).put("position", position.toJSONArray()).put("rotation", rotation.toJSONArray()).put("scale", scale.toJSONArray()))
            NetworkManager.connections.forEach { it.sendRaw(packet) }
        }
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

    fun setTransformSilent(position: Vector3, rotation: Vector3, scale: Vector3) {
        this.rotation.set(rotation)
        this.position.set(position)
        this.scale.set(scale)
        updateInstanceTransform(silent = true)
    }

    inline fun <reified T: EntityComponent> getComponentOfType(): T? {
        return getComponents().firstOrNull { it is T } as? T
    }
}

fun Vector3.toJSONArray(): JSONArray {
    return JSONArray()
        .put(this.x)
        .put(this.y)
        .put(this.z)
}
