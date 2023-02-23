package webstreamengine.client.ui.microelements

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import org.json.JSONObject
import webstreamengine.client.managers.TextureManager
import webstreamengine.client.ui.*

class ImageElement(
    id: String,
    key: String,
    private val scale: Float = 1f,
    verticalAlignment: VerticalAlignment = VerticalAlignment.CENTER,
    horizontalAlignment: HorizontalAlignment = HorizontalAlignment.CENTER
): UIElement(id, verticalAlignment, horizontalAlignment) {

    private var texture: Texture? = null

    constructor(jsonObject: JSONObject, id: String, va: VerticalAlignment, ha: HorizontalAlignment): this(
        id,
        jsonObject.getString("key") ?: throw IllegalArgumentException("A key must be provided to an image ui element"),
        jsonObject.optFloat("size", 1f),
        va, ha
    )

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
        if (texture == null) return
        batch.draw(texture, x * Gdx.graphics.width, y * Gdx.graphics.height, width * Gdx.graphics.width, height * Gdx.graphics.height)
    }
}