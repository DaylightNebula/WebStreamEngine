package webstreamengine.client.tasks

import org.json.JSONObject
import webstreamengine.client.entities.Entity

object TaskRegistry {
    private val constructors = hashMapOf<String, (entity: Entity, json: JSONObject) -> Task>()

    fun registerTask(id: String, c: (entity: Entity, json: JSONObject) -> Task) {
        constructors[id] = c
    }

    fun createTaskByJSON(entity: Entity, json: JSONObject): Task {
        val type = json.optString("type") ?: throw IllegalArgumentException("Type argument required in task json object")
        if (!constructors.containsKey(type)) throw IllegalArgumentException("No task with type $type registered")
        return constructors[type]!!(entity, json)
    }
}