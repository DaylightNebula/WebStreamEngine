package webstreamengine.client.entities

import com.badlogic.gdx.graphics.Color
import org.json.JSONObject
import webstreamengine.client.entities.components.*
import webstreamengine.client.tasks.TaskComponent

object EntityComponentRegistry {
    private val constructorMap = hashMapOf<String, (entity: Entity, json: JSONObject) -> EntityComponent?>()

    init {
        registerComponent("model") { entity, json ->
            entity.addModelComponent(json.getString("key")
                ?: throw IllegalArgumentException("Key argument must be added to model component json"))
            null
        }
        registerComponent("sound") { entity, _ -> SoundComponent(entity) }
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