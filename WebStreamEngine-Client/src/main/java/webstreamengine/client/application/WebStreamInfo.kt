package webstreamengine.client.application

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import webstreamengine.client.entities.Entity

object WebStreamInfo {
    lateinit var cam: PerspectiveCamera
    val environment = Environment()
    val entities = mutableListOf<Entity>()

    fun initCamera() {
        // create basic camera
        cam = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        cam.position.set(0f, 0f, 0f)
        cam.lookAt(0f, 0f, 10f)
        cam.near = .1f
        cam.far = 1000f
        cam.update()
    }

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