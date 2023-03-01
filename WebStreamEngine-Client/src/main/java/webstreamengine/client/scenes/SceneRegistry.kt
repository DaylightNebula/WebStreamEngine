package webstreamengine.client.scenes

import com.badlogic.gdx.Net
import com.badlogic.gdx.math.Vector3
import org.json.JSONObject
import webstreamengine.client.networking.FuelClient
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.EntityHandler
import webstreamengine.client.networking.Connection
import webstreamengine.client.networking.NetworkManager
import webstreamengine.client.ui.UIManager
import webstreamengine.client.ui.UserInterface
import java.util.*

object SceneRegistry {
    private val constructors = hashMapOf<String, () -> Scene>()
    private var currentScene: Scene? = null

    fun registerScene(id: String, c: () -> Scene) {
        println("Scene registered $id")
        constructors[id] = c
    }

    fun isSceneRegistered(id: String): Boolean {
        return constructors[id] != null
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
            currentScene?.generalStop()

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

                val path = json.getString("path")

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
                Entity.createFromPath(UUID.randomUUID(), true, path, position, rotation, scale) {}
            }

            // update the tracker and start the new scene
            currentScene = scene
            scene.generalStart()
        }
    }

    fun serverUpdate() {
        currentScene?.serverUpdate()
    }

    fun clientUpdate() {
        currentScene?.clientUpdate()
    }

    fun handleNetJoin(conn: Connection) {
        if (NetworkManager.isActive && NetworkManager.isServer)
            currentScene?.netJoin(conn)
    }

    fun handleDisconnect(conn: Connection) {
        if (NetworkManager.isActive && NetworkManager.isServer)
            currentScene?.netDisconnect(conn)
    }
}