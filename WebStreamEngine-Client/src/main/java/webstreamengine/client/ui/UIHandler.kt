package webstreamengine.client.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

object UIHandler {
    
    private val stage = Stage()
    private val elements = mutableListOf<UIElement>()
    private val waitingForActor = mutableListOf<UIElement>()
    
    fun init() {
        // set stage as an active input processor
        Gdx.input.inputProcessor = stage
    }

    fun addUIElement(element: UIElement) {
        waitingForActor.add(element)
    }

    fun removeUIElement(element: UIElement) {
        elements.remove(element)
        waitingForActor.remove(element)
        element.actor?.remove()
    }

    fun clearUI() {
        stage.clear()
        elements.clear()
        waitingForActor.clear()
    }
    
    fun renderUI() {
        // for each element that is waiting for an actor, check if it has an actor, if so, move it to the active elements list and add it to the active stage
        waitingForActor.forEach { element ->
            if (element.isReady()) {
                elements.add(element)
                stage.addActor(element.actor!!)
            }
        }
        waitingForActor.removeIf { it.isReady() }

        // draw stage
        stage.draw()
    }
    
    fun dispose() {
        stage.dispose()
    }
}