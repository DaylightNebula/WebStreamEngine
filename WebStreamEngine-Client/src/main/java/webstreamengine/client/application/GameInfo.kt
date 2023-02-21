package webstreamengine.client.application

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import webstreamengine.client.controller.Controller
import webstreamengine.client.controller.ControllerSettings
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.EntityComponent

object GameInfo {
    lateinit var cam: PerspectiveCamera
    val environment = Environment()
    val controller = Controller(ControllerSettings.genStaticSettings())
    var currentSeconds = 0f

    fun initCamera() {
        // create basic camera
        cam = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        cam.position.set(0f, 0f, 0f)
        cam.lookAt(0f, 0f, 10f)
        cam.near = .1f
        cam.far = 1000f
        cam.update()
    }

    fun setAmbientLight(color: Color) {
        environment.set(
            ColorAttribute(
                ColorAttribute.AmbientLight,
                color
            )
        )
    }

    fun update() {
        controller.update()
        currentSeconds += Gdx.graphics.deltaTime
    }
}