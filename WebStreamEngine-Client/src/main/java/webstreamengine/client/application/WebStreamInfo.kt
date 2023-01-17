package webstreamengine.client.application

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import webstreamengine.client.entities.Entity

object WebStreamInfo {
    val environment = Environment()
    val entities = mutableListOf<Entity>()

    fun addEntity(entity: Entity) {
        entities.add(entity)
    }

    fun removeEntity(entity: Entity) {
        entities.remove(entity)
    }

    fun setAmbientLight(color: Color) {
        environment.set(
            ColorAttribute(
                ColorAttribute.AmbientLight,
                color
            )
        )
    }
}