package webstreamengine.client.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

abstract class UIElement {
    var actor: Actor? = null

    fun isReady(): Boolean {
        return actor != null
    }
}
abstract class UIBoundedElement(
    private var xFraction: Float, private var yFraction: Float,
    private var widthFraction: Float, private var heightFraction: Float,
): UIElement() {
    fun setActorToSourceBounds() {
        setBounds(xFraction, yFraction, widthFraction, heightFraction)
    }

    fun setPosition(xFraction: Float, yFraction: Float) {
        this.xFraction = xFraction
        this.yFraction = yFraction
        actor?.setPosition(
            Gdx.graphics.width * xFraction,
            Gdx.graphics.height * yFraction
        )
    }

    fun setDimensions(widthFraction: Float, heightFraction: Float) {
        this.widthFraction = widthFraction
        this.heightFraction = heightFraction
        actor?.width = Gdx.graphics.height * widthFraction
        actor?.height = Gdx.graphics.height * heightFraction
    }

    fun setBounds(xFraction: Float, yFraction: Float, widthFraction: Float, heightFraction: Float) {
        setPosition(xFraction, yFraction)
        setDimensions(widthFraction, heightFraction)
    }
}