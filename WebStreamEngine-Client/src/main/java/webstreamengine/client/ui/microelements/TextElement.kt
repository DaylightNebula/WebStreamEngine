package webstreamengine.client.ui.microelements

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.math.Vector2
import org.json.JSONObject
import webstreamengine.client.managers.FontManager
import webstreamengine.client.ui.HorizontalAlignment
import webstreamengine.client.ui.UIElement
import webstreamengine.client.ui.UIManager
import webstreamengine.client.ui.VerticalAlignment

class TextElement(
    id: String,
    private val info: FontInfo,
    private var text: String,
    verticalAlignment: VerticalAlignment = VerticalAlignment.CENTER,
    horizontalAlignment: HorizontalAlignment = HorizontalAlignment.CENTER
): UIElement(id, verticalAlignment, horizontalAlignment) {

    constructor(json: JSONObject, id: String, va: VerticalAlignment, ha: HorizontalAlignment): this(
        id,
        FontInfo(
            json.getString("key") ?: throw IllegalArgumentException("Key is required in text ui elements"),
            json.getInt("size") ?: throw IllegalArgumentException("Size is required in text ui elements"),
            Color.valueOf(json.getString("color") ?: throw IllegalArgumentException("Color is required in text ui elements"))
        ),
        json.optString("text", ""),
        va, ha
    )

    private var font: BitmapFont? = null
    private var bounds: Vector2? = null

    init {
        FontManager.makeIDExist(info.key)
    }

    private fun setup(handle: FileHandle) {
        // create font
        val generator = FreeTypeFontGenerator(handle) // todo move generator to font manager
        val parameter = FreeTypeFontParameter().apply {
            this.size = info.size
            this.color = info.color
        }
        font = generator.generateFont(parameter)

        // get bounds
        val layout = GlyphLayout()
        layout.setText(font, text)
        bounds = Vector2(layout.width, layout.height)

        // get rid of the generator
        generator.dispose()
    }

    override fun getRequestedSize(): Vector2 {
        return Vector2((bounds?.x ?: 0f) / Gdx.graphics.width, (bounds?.y ?: 0f) / Gdx.graphics.height)
    }

    override fun renderToBounds(batch: SpriteBatch) {
        if (font == null) FontManager.getFont(info.key)?.let { setup(it); UIManager.isDirty = true }
        font?.draw(batch, text, x * Gdx.graphics.width /*- ((bounds?.x ?: 0f) / 2f)*/, y * Gdx.graphics.height + (bounds?.y ?: 0f))
    }
}
data class FontInfo(val key: String, val size: Int, val color: Color)
fun Color.valueOf(hex: String): Color {
    var hex = hex
    hex = if (hex[0] == '#') hex.substring(1) else hex
    val r = Integer.valueOf(hex.substring(0, 2), 16)
    val g = Integer.valueOf(hex.substring(2, 4), 16)
    val b = Integer.valueOf(hex.substring(4, 6), 16)
    val a = if (hex.length != 8) 255 else Integer.valueOf(hex.substring(6, 8), 16)
    return Color(r / 255f, g / 255f, b / 255f, a / 255f)
}