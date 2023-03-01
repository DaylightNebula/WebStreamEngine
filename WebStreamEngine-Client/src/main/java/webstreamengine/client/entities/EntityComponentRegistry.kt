package webstreamengine.client.entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import org.json.JSONObject
import webstreamengine.client.controller.PlayerControllerComponent
import webstreamengine.client.entities.components.*
import webstreamengine.client.physics.ColliderComponent
import webstreamengine.client.physics.SimpleBox
import webstreamengine.client.tasks.TaskComponent

object EntityComponentRegistry {
    private val constructorMap = hashMapOf<String, (entity: Entity, json: JSONObject) -> EntityComponent?>()

    init {
        registerComponent("model") { entity, json ->
            ModelComponent(entity, json.getString("key")
                ?: throw IllegalArgumentException("Key argument must be added to model component json"))
        }
        registerComponent("collider") { entity, json ->
            ColliderComponent(
                entity,
                SimpleBox(
                    if (json.has("center")) Vector3(json.getJSONArray("center").getFloat(0), json.getJSONArray("center").getFloat(1), json.getJSONArray("center").getFloat(2)) else Vector3(0f, 0f, 0f),
                    Vector3(
                        json.getJSONArray("size").getFloat(0),
                        json.getJSONArray("size").getFloat(1),
                        json.getJSONArray("size").getFloat(2)
                    )
                ),
                json.optBoolean("gravity", true),
                json.optBoolean("rayCastOnly", false)
            )
        }
        registerComponent("point_light") { entity, json ->
            PointLightComponent(
                entity,
                Color.valueOf(json.getString("color") ?: throw IllegalArgumentException("Color argument must be added to point light component json")),
                json.optFloat("intensity", 1f)
            )
        }
        registerComponent("spot_light") { entity, json ->
            SpotLightComponent(
                entity,
                Color.valueOf(json.getString("color") ?: throw IllegalArgumentException("Color argument must be added to point light component json")),
                json.optFloat("cutOffAngle", 30f),
                json.optFloat("intensity", 1f),
                json.optFloat("exponent", 1f)
            )
        }
        registerComponent("directional_light") { entity, json ->
            DirectionalLightComponent(
                entity,
                Color.valueOf(json.getString("color") ?: throw IllegalArgumentException("Color argument must be added to point light component json"))
            )
        }
        registerComponent("tasks") { entity, json ->
            TaskComponent(
                entity,
                json.optJSONArray("tasks") ?: throw IllegalArgumentException("Tasks array must be added to tasks component json")
            )
        }
        registerComponent("controller") { entity, json ->
            PlayerControllerComponent(
                entity,
                json.optBoolean("canZoom", false),
                json.optBoolean("canRotate", false),
                json.optBoolean("lockMouse", false),
                json.optString("moveInputStick", ""),
                json.optVector3("defaultRotation", Vector3.Zero),
                json.optFloat("distanceFromRoot", 0f),
                json.optFloat("movementSpeed", 0f),
                json.optFloat("minDistanceFromRoot", 0f),
                json.optFloat("maxDistanceFromRoot", json.optFloat("distanceFromRoot", 0f)),
                json.optVector3("positionOffset", Vector3.Zero)
            )
        }
    }

    fun createComponentViaJSON(entity: Entity, json: JSONObject): EntityComponent? {
        val type = json.getString("type")
        if (!constructorMap.containsKey(type)) throw IllegalArgumentException("No component with json id $type registered")
        return constructorMap[type]!!(entity, json)
    }

    fun registerComponent(jsonID: String, createFunc: (entity: Entity, json: JSONObject) -> EntityComponent?) {
        constructorMap[jsonID] = createFunc
    }
}

fun JSONObject.getVector3(name: String): Vector3? {
    val arr = this.optJSONArray(name) ?: return null
    if (arr.length() < 3) return null
    return Vector3(
        arr.getFloat(0),
        arr.getFloat(1),
        arr.getFloat(2),
    )
}

fun JSONObject.optVector3(name: String, other: Vector3): Vector3 {
    return getVector3(name) ?: other
}