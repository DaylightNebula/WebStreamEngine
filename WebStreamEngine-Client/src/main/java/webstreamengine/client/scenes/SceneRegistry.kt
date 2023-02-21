package webstreamengine.client.scenes

import com.badlogic.gdx.math.Vector3
import org.json.JSONObject
import webstreamengine.client.JarInterface
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.EntityChunks
import webstreamengine.client.entities.EntityComponentRegistry
import webstreamengine.client.ui.UIManager
import webstreamengine.client.ui.UserInterface

object SceneRegistry {
    private val constructors = hashMapOf<String, () -> Scene>()
    private var currentScene: Scene? = null

    fun registerScene(id: String, c: () -> Scene) {
        constructors[id] = c
    }

    fun loadScene(id: String) {
        // get and create new scene
        if (!constructors.containsKey(id)) throw IllegalArgumentException("No scene registered with id $id")
        val scene = constructors[id]!!()

        // stop the current scene
        currentScene?.stop()

        // clear old ui and entities if necessary
        if (currentScene != null) UIManager.clearScripts()
        if (currentScene != null) EntityChunks.clear()

        // load scene json
        val json = JSONObject(
            JarInterface.getTextResource("scenes/${scene.jarpath}.json")
                ?: throw IllegalArgumentException("Unable to load scene json on jar path ${scene.jarpath}")
        )

        // load uis
        json.getJSONArray("uis").forEach { str ->
            UserInterface.loadInterface(str as String)
        }

        // load entities
        json.getJSONArray("entities").forEach { j ->
            // load basic json
            val json = j as JSONObject
            val id = json.getString("id")

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
            EntityChunks.addEntity(
                Entity(
                    path = id,
                    position = position,
                    rotation = rotation,
                    scale = scale
                )
            )
        }

        // update the tracker and start the new scene
        currentScene = scene
        scene.start()
    }

    fun updateScene() {
        currentScene?.update()
    }
}