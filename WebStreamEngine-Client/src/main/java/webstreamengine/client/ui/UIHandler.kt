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
    
    fun init() {
        // set stage as an active input processor
        Gdx.input.inputProcessor = stage
    }

    fun addUIElement(element: UIElement) {
        stage.addActor(element.actor)
    }

    fun removeUIElement(element: UIElement) {
        element.actor.remove()
    }

    fun clearUI() {
        stage.clear()
    }
    
    fun renderUI() {
        stage.draw()
    }
    
    fun dispose() {
        stage.dispose()
    }
}