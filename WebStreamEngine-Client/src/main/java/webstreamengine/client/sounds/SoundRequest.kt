package webstreamengine.client.sounds

import com.badlogic.gdx.math.Vector3
import webstreamengine.client.Renderer
import webstreamengine.client.application.GameInfo

data class SoundRequest(
    val soundID: String,
    val baseVolume: Float,
    val pitch: Float,
    val pan: Float,

    val position: Vector3? = null,
    val startTime: Float = Renderer.currentSeconds
)