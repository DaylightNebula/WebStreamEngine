package webstreamengine.client.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

abstract class UIElement(val actor: Actor)
abstract class UIBoundedElement(
    actor: Actor,
    xFraction: Float, yFraction: Float,
    widthFraction: Float, heightFraction: Float,
): UIElement(actor) {
    init {
        // setup actor with the given position and dimension information
        actor.setPosition(
            Gdx.graphics.width * xFraction,
            Gdx.graphics.height * yFraction
        )
        actor.width = Gdx.graphics.height * widthFraction
        actor.height = Gdx.graphics.height * heightFraction
    }

    fun setPosition(xFraction: Float, yFraction: Float) {
        actor.setPosition(
            Gdx.graphics.width * xFraction,
            Gdx.graphics.height * yFraction
        )
    }

    fun setDimensions(widthFraction: Float, heightFraction: Float) {
        actor.width = Gdx.graphics.height * widthFraction
        actor.height = Gdx.graphics.height * heightFraction
    }

    fun setBounds(xFraction: Float, yFraction: Float, widthFraction: Float, heightFraction: Float) {
        setPosition(xFraction, yFraction)
        setDimensions(widthFraction, heightFraction)
    }
}