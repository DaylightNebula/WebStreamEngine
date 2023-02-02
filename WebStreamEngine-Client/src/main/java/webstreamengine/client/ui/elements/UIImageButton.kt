package webstreamengine.client.ui.elements

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import webstreamengine.client.managers.TextureManager
import webstreamengine.client.ui.UIBoundedElement
import webstreamengine.client.ui.UIElement

class UIImageButton(
    textureID: String,
    private val xFraction: Float, private val yFraction: Float,
    private val widthFraction: Float, private val heightFraction: Float,
    val clickCallback: (button: UIImageButton) -> Unit
): UIElement() {
    init {
        // ask texture manager for the given texture
        TextureManager.applyTextureToTarget(this, textureID)
    }

    fun handleTextureAssign(texture: Texture) {
        // get dimensions
        val x = Gdx.graphics.width * xFraction
        val y = Gdx.graphics.height * yFraction
        val width = Gdx.graphics.width * widthFraction
        val height = Gdx.graphics.height * heightFraction

        // setup actor
        val texRegion = TextureRegionDrawable(texture)
        texRegion.setMinSize(width, height)
        val button = ImageButton(texRegion)
        button.setBounds(x, y, width, height)
        actor = button

        // add proper callback to the actor
        val me = this
        (actor as ImageButton).addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                clickCallback(me)
            }
        })
    }
}