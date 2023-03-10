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
import net.mgsx.gltf.loaders.glb.GLBLoader
import net.mgsx.gltf.loaders.gltf.GLTFLoader
import webstreamengine.client.networking.FuelClient
import webstreamengine.client.Renderer
import webstreamengine.client.entities.Entity
import webstreamengine.client.headless
import java.io.File

object ModelManager {

    private val modelMap = hashMapOf<String, Model>() // Format: ID, Model
    private val requestedIDs = mutableListOf<String>()

    // loaders
    private val builder = ModelBuilder()
    private val loader = G3dModelLoader(JsonReader())
    private val gltfLoader = GLTFLoader()
    private val glbLoader = GLBLoader()

    private fun isIDInUse(id: String): Boolean {
        return modelMap.containsKey(id) || requestedIDs.contains(id)
    }

    fun update() {
    }

    fun requestIfNecessary(id: String, isHighPrio: Boolean, callback: (key: String) -> Unit) {
        if (headless) return

        // if we already have the model, just run the callback
        if (modelMap.containsKey(id)) return

        // if we have a locally stored model for the id, load that and then run the callback
        val modelFile = File(System.getProperty("user.dir"), "cache/$id.g3dj")
        if (modelFile.exists()) return

        // if we made this far, check if we have not requested the id
        if (!requestedIDs.contains(id)) {
            // if high priority, ask now, otherwise add to the low prio queue
            askForDelivery(id)
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
        // get file handle and make sure it exists
        val file = Renderer.getGdxFile(path)
        if (!file.exists()) return null

        // load model based on its type
        val model = when(file.extension()) {
            "gltf" -> gltfLoader.load(file).scene.model
            "glb" -> glbLoader.load(file).scene.model
            else -> loader.loadModel(file)
        }

        // save and return model
        modelMap[id] = model
        return model
    }

    private fun askForDelivery(id: String) {
        if (headless) return

        requestedIDs.add(id)
        FuelClient.requestFile(id) {}
    }

    private fun createModelInstance(id: String): ModelInstance? {
        return ModelInstance(modelMap[id] ?: return null)
    }

    fun getModelByKey(key: String): Model? = modelMap[key] ?: FuelClient.getPreexistingFile(key)?.let { loadLocal(key, it.absolutePath) }

    fun dispose() {
        // dispose of all models
        modelMap.values.forEach { it.dispose() }
    }
}