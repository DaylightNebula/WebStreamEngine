package webstreamengine.client.managers

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
import webstreamengine.client.FuelClient
import webstreamengine.client.Renderer
import webstreamengine.client.entities.Entity
import webstreamengine.client.headless
import java.io.File

object ModelManager {

    private val modelMap = hashMapOf<String, Model>() // Format: ID, Model
    private val requestedIDs = mutableListOf<String>()
    private val waitingForModel = hashMapOf<String, MutableList<Entity>>()
    private val lowPrioQueue = mutableListOf<String>()
    private val runOnDeliver = hashMapOf<String, MutableList<(key: String) -> Unit>>()

    // loaders
    private val builder = ModelBuilder()
    private val loader = G3dModelLoader(JsonReader())

    private fun isIDInUse(id: String): Boolean {
        return modelMap.containsKey(id) || requestedIDs.contains(id)
    }

    fun update() {
        // if we are not waiting for any models and the low priority queue has something in it, ask for the next thing
        if (requestedIDs.isEmpty() && lowPrioQueue.isNotEmpty())
            askForDelivery(lowPrioQueue.first())
    }

    fun requestIfNecessary(id: String, isHighPrio: Boolean, callback: (key: String) -> Unit) {
        if (headless) return

        // if we already have the model, just run the callback
        if (modelMap.containsKey(id)) return

        // if we have a locally stored model for the id, load that and then run the callback
        val modelFile = File(System.getProperty("user.dir"), "cache/$id.g3dj")
        if (modelFile.exists()) return

        // add to run on delivery list
        var list = runOnDeliver[id]
        if (list == null) {
            list = mutableListOf()
            runOnDeliver[id] = list
        }
        list.add(callback)

        // if we made this far, check if we have not requested the id
        if (!requestedIDs.contains(id)) {
            // if high priority, ask now, otherwise add to the low prio queue
            if (isHighPrio)
                askForDelivery(id)
            else
                lowPrioQueue.add(id)
        }
    }

    fun createTestBox(id: String, dimensions: Vector3, color: Color) {
        if (headless) return

        // make sure we don't already have something of the current id
        if (isIDInUse(id)) return

        // add a box to the model map created from the given info
        modelMap[id] = builder.createBox(
            dimensions.x, dimensions.y, dimensions.z,
            Material(ColorAttribute.createDiffuse(color)),
            (Usage.Position or Usage.Normal).toLong()
        )
    }

    private fun loadLocal(id: String, path: String): Model? {
        val file = Renderer.getGdxFile(path)
        if (!file.exists()) return null
        val model = loader.loadModel(file)
        modelMap[id] = model
        return model
    }

    fun applyModelToEntity(entity: Entity, id: String) {
        if (headless) return

        // if we already have a model with the given id, just pass it along
        if (modelMap.containsKey(id)) {
            return
        }

        // check if the cache has a file for the given id, if so load that
        val modelFile = File(System.getProperty("user.dir"), "cache/$id.g3dj")
        if (modelFile.exists()) {
            loadLocal(id, modelFile.absolutePath)
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
            askForDelivery(id)
        }
    }

    private fun askForDelivery(id: String) {
        if (headless) return

        requestedIDs.add(id)
        FuelClient.requestFile("$id.g3dj") { handleModelDelivery(id, it) }
    }

    private fun handleModelDelivery(id: String, file: File) {
        if (headless) return

        Renderer.runOnGdxThread {
            // load file
            loadLocal(id, file.absolutePath)

            // remove requested id
            requestedIDs.remove(id)

            // update all entities waiting for this mesh
            val model = modelMap[id]!!
            waitingForModel[id]?.clear()
        }
    }

    private fun createModelInstance(id: String): ModelInstance? {
        return ModelInstance(modelMap[id] ?: return null)
    }

    fun getModelByKey(key: String): Model? = modelMap[key] ?: loadLocal(key, File("cache/$key.g3dj").absolutePath)

    fun dispose() {
        // dispose of all models
        modelMap.values.forEach { it.dispose() }
    }
}