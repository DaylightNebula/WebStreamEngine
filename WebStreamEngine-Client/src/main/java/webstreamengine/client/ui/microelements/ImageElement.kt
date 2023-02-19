package webstreamengine.client.ui.microelements

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import webstreamengine.client.managers.TextureManager
import webstreamengine.client.ui.*

class ImageElement(
    key: String,
    private val scale: Float = 1f,
    verticalAlignment: VerticalAlignment = VerticalAlignment.CENTER,
    horizontalAlignment: HorizontalAlignment = HorizontalAlignment.CENTER,
    clickUp: (() -> Unit)? = null,
    clickDown: (() -> Unit)? = null
): UIElement(verticalAlignment, horizontalAlignment, clickUp, clickDown) {

    private var texture: Texture? = null

    init {
        TextureManager.applyTextureToTarget(this, key)
    }

    fun handleTextureAssign(texture: Texture) {
        this.texture = texture
        UIManager.isDirty = true
    }

    override fun getRequestedSize(): Vector2 {
        if (texture == null) return Vector2.Zero

        return Vector2(
            (texture!!.width.toFloat() / Gdx.graphics.width) * scale,
            (texture!!.height.toFloat() / Gdx.graphics.height) * scale
        )
    }

    override fun renderToBounds(batch: SpriteBatch) {
        batch.draw(texture, x * Gdx.graphics.width, y * Gdx.graphics.height, width * Gdx.graphics.width, height * Gdx.graphics.height)
    }
}