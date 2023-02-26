package webstreamengine.client.entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import org.json.JSONObject
import webstreamengine.client.entities.components.*
import webstreamengine.client.physics.ColliderComponent
import webstreamengine.client.physics.SimpleBox
import webstreamengine.client.tasks.TaskComponent

object EntityComponentRegistry {
    private val constructorMap = hashMapOf<String, (entity: Entity, json: JSONObject) -> EntityComponent?>()

    init {
        registerComponent("model") { entity, json ->
            entity.addModelComponent(json.getString("key")
                ?: throw IllegalArgumentException("Key argument must be added to model component json"))
            null
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