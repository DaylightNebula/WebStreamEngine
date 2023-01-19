package webstreamengine.client.entities.components

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g3d.ModelBatch
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.EntityComponent
import webstreamengine.client.managers.SoundManager

class SoundComponent(entity: Entity): EntityComponent(entity) {

    fun playSound(id: String) {
        SoundManager.applySoundToComponent(this, id)
    }

    fun handleSound(id: String, sound: Sound) {
        sound.play(1f)
    }

    override fun start() {}
    override fun update() {}
    override fun render(batch: ModelBatch) {}
    override fun stop() {}
}