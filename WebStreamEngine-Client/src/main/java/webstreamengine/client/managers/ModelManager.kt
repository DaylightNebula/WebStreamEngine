package webstreamengine.client.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.JsonReader
import webstreamengine.client.conn
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.components.ModelComponent
import webstreamengine.core.ByteUtils
import webstreamengine.core.PacketType
import webstreamengine.core.PacketUtils
import java.io.File

object ModelManager {

    private val modelMap = hashMapOf<String, Model>() // Format: ID, Model
    private val requestedIDs = mutableListOf<String>()
    private val waitingForModel = hashMapOf<String, MutableList<Entity>>()

    // loaders
    val builder = ModelBuilder()
    val loader = G3dModelLoader(JsonReader())

    fun isIDInUse(id: String): Boolean {
        return modelMap.containsKey(id) || requestedIDs.contains(id)
    }

    fun createTestBox(id: String, dimensions: Vector3, color: Color) {
        // make sure we don't already have something of the current id
        if (isIDInUse(id)) return

        // add a box to the model map created from the given info
        modelMap[id] = builder.createBox(
            dimensions.x, dimensions.y, dimensions.z,
            Material(ColorAttribute.createDiffuse(color)),
            (Usage.Position or Usage.Normal).toLong()
        )
    }

    fun loadLocal(id: String, path: String) {
        modelMap[id] = loader.loadModel(Gdx.files.absolute(path))
    }

    fun applyModelToEntity(entity: Entity, id: String) {
        // if we already have a model with the given id, just pass it along
        if (modelMap.containsKey(id)) {
            entity.addComponent(ModelComponent(entity, createModelInstance(id)!!))
            return
        }

        // check if the cache has a file for the given id, if so load that
        val modelFile = File(System.getProperty("user.dir"), "cache/$id.g3dj")
        if (modelFile.exists()) {
            loadLocal(id, modelFile.absolutePath)
            entity.addComponent(ModelComponent(entity, createModelInstance(id)!!))
            return
        }

        // add to waiting list
        var list = waitingForModel[id]
        if (list == null) {
            list = mutableListOf()
            waitingForModel[id] = list
        }
        list.add(entity)

        // if not in requested list, send a request to the server for the given model
        if (!requestedIDs.contains(id)) {
            requestedIDs.add(id)
            conn.sendPacket(
                PacketUtils.generatePacket(
                    PacketType.REQUEST_MODEL,
                    ByteUtils.convertStringToByteArray(id)
                )
            )
        }
    }

    fun handleModelDelivery(id: String, bytes: ByteArray) {
        // write file bytes to a cache file
        val file = File(System.getProperty("user.dir"), "cache/$id.g3dj")
        file.parentFile.mkdirs()
        file.writeBytes(bytes)

        // load file
        loadLocal(id, file.absolutePath)

        // remove requested id
        requestedIDs.remove(id)

        // update all entities waiting for this mesh
        val model = modelMap[id]!!
        waitingForModel[id]?.forEach { it.addComponent(ModelComponent(it, ModelInstance(model))) }
        waitingForModel[id]?.clear()
    }

    fun createModelInstance(id: String): ModelInstance? {
        return ModelInstance(modelMap[id] ?: return null)
    }

    fun dispose() {
        // dispose of all models
        modelMap.values.forEach { it.dispose() }
    }
}