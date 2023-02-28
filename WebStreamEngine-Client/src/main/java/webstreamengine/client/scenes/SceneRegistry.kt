package webstreamengine.client.scenes

import com.badlogic.gdx.math.Vector3
import org.json.JSONObject
import webstreamengine.client.networking.FuelClient
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.EntityHandler
import webstreamengine.client.ui.UIManager
import webstreamengine.client.ui.UserInterface

object SceneRegistry {
    private val constructors = hashMapOf<String, () -> Scene>()
    private var currentScene: Scene? = null

    fun registerScene(id: String, c: () -> Scene) {
        constructors[id] = c
    }

    fun loadScene(masterID: String) {
        // get and create new scene
        if (!constructors.containsKey(masterID)) throw IllegalArgumentException("No scene registered with id $masterID")
        val scene = constructors[masterID]!!()

        // load scene json
        FuelClient.requestFile("$masterID.scene") {
            // load json
            val masterJson = JSONObject(it.readText())
            println("Loaded scene $masterJson")

            // stop the current scene
            currentScene?.stop()

            // clear old ui and entities if necessary
            if (currentScene != null) UIManager.clearScripts()
            if (currentScene != null) EntityHandler.clear()

            // load uis
            masterJson.getJSONArray("uis").forEach { str ->
                UserInterface.loadInterface(str as String)
            }

            // load entities
            masterJson.getJSONArray("entities").forEach { j ->
                // load basic json
                val json = j as JSONObject
                val id = json.getString("id")
                println("Attempting to make entity $id")

                // load arrays for vectors
                val positionArr = json.optJSONArray("position")
                val rotationArr = json.optJSONArray("rotation")
                val scaleArr = json.optJSONArray("scale")

                // load vectors
                val position = if (positionArr != null)
                    Vector3(positionArr.getFloat(0), positionArr.getFloat(1), positionArr.getFloat(2))
                else Vector3(0f, 0f, 0f)
                val rotation = if (rotationArr != null)
                    Vector3(rotationArr.getFloat(0), rotationArr.getFloat(1), rotationArr.getFloat(2))
                else Vector3(0f, 0f, 0f)
                val scale = if (scaleArr != null)
                    Vector3(scaleArr.getFloat(0), scaleArr.getFloat(1), scaleArr.getFloat(2))
                else Vector3(1f, 1f, 1f)

                // register the entity
                Entity.createFromPath(id, position, rotation, scale)
            }

            // update the tracker and start the new scene
            currentScene = scene
            scene.start()
        }
    }

    fun updateScene() {
        currentScene?.update()
    }
}