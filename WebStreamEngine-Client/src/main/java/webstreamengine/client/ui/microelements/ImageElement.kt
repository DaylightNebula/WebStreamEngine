package webstreamengine.client.ui.microelements

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import org.json.JSONObject
import webstreamengine.client.Renderer
import webstreamengine.client.managers.TextureManager
import webstreamengine.client.ui.*

class ImageElement(
    id: String,
    private val key: String,
    private val scale: Float = 1f,
    verticalAlignment: VerticalAlignment = VerticalAlignment.CENTER,
    horizontalAlignment: HorizontalAlignment = HorizontalAlignment.CENTER
): UIElement(id, verticalAlignment, horizontalAlignment) {


    constructor(jsonObject: JSONObject, id: String, va: VerticalAlignment, ha: HorizontalAlignment): this(
        id,
        jsonObject.getString("key") ?: throw IllegalArgumentException("A key must be provided to an image ui element"),
        jsonObject.optFloat("size", 1f),
        va, ha
    )

    init {
        TextureManager.requestTextureIfNecessary(key)
    }

    override fun getRequestedSize(): Vector2 {
        val texture = TextureManager.getTextureIfAvailable(key) ?: return Vector2.Zero

        return Vector2(
            (texture.width.toFloat() / Gdx.graphics.width) * scale,
            (texture.height.toFloat() / Gdx.graphics.height) * scale
        )
    }

    override fun renderToBounds(batch: SpriteBatch) {
        // try to get texture, do not render if no texture
        val texture = TextureManager.getTextureIfAvailable(key) ?: return

        // calculate final position and size of this element
        val position = Vector2(x * Gdx.graphics.width, y * Gdx.graphics.height)
        val size = Vector2(width * Gdx.graphics.width, height * Gdx.graphics.height)

        // render the image
        Renderer.renderImage(texture, position, size)
    }
}