package webstreamengine.client.application

import webstreamengine.client.entities.Entity

object WebStreamInfo {
    val entities = mutableListOf<Entity>()

    fun addEntity(entity: Entity) {
        entities.add(entity)
    }

    fun removeEntity(entity: Entity) {
        entities.remove(entity)
    }
}