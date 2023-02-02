package webstreamengine.client.ui.elements

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import webstreamengine.client.managers.TextureManager
import webstreamengine.client.ui.UIElement

class UIImage (
    textureID: String,
    private val xFraction: Float, private val yFraction: Float,
    private val widthFraction: Float, private val heightFraction: Float
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
        val image = Image(texRegion)
        image.setBounds(x, y, width, height)
        actor = image
    }
}