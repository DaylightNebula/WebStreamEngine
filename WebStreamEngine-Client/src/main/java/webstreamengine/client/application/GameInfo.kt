package webstreamengine.client.application

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute

object GameInfo {
    val environment = Environment()

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
}