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
    val environment = Environment()
    val controller = Controller(ControllerSettings.genStaticSettings())

    fun initCamera() {
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
    }
}