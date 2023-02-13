package webstreamengine.client.entities.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.environment.BaseLight
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.environment.PointLight
import com.badlogic.gdx.graphics.g3d.environment.SpotLight
import webstreamengine.client.application.GameInfo
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.EntityComponent
import kotlin.math.cos
import kotlin.math.sin

open class LightComponent(entity: Entity, val light: BaseLight<*>): EntityComponent(entity) {

    init {
        // add light to environment
        GameInfo.environment.add(light)

        // update light location when entity moves, as well as now
        updateLightTransform()
        entity.transformChangeCallbacks.add { updateLightTransform() }
    }

    fun updateLightTransform() {
        // only point light and spotlight and directional light need position and/or directions updated
        if (light is PointLight) {
            light.setPosition(entity.getPosition())
        } else if (light is SpotLight) {
            light.setPosition(entity.getPosition())
            val rot = entity.getRotation()
            light.setDirection(
                cos(rot.z) * cos(rot.x),
                sin(rot.z) * cos(rot.x),
                sin(rot.x)
            )
        } else if (light is DirectionalLight) {
            val rot = entity.getRotation()
            light.setDirection(
                cos(rot.z) * cos(rot.x),
                sin(rot.z) * cos(rot.x),
                sin(rot.x)
            )
        }
    }

    override fun stop() {
        // remove light from environment
        GameInfo.environment.remove(light)
    }

    override fun start() {}
    override fun update() {}
    override fun render(batch: ModelBatch) {}
}
class PointLightComponent(entity: Entity, color: Color, intensity: Float): LightComponent(
    entity,
    PointLight().apply {
        this.setColor(color)
        this.setIntensity(intensity)
    }
)
class SpotLightComponent(entity: Entity, color: Color, cutOffAngle: Float, intensity: Float, exponent: Float = 1f): LightComponent(
    entity,
    SpotLight().apply {
        this.setColor(color)
        this.setIntensity(intensity)
        this.setCutoffAngle(cutOffAngle)
        this.setExponent(exponent)
    }
)
class DirectionalLightComponent(entity: Entity, color: Color): LightComponent(
    entity,
    DirectionalLight().apply {
        this.setColor(color)
    }
)