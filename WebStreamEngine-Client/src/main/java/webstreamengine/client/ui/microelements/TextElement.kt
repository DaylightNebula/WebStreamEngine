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
import webstreamengine.client.managers.FontManager
import webstreamengine.client.ui.HorizontalAlignment
import webstreamengine.client.ui.UIElement
import webstreamengine.client.ui.VerticalAlignment

class TextElement(
    val info: FontInfo,
    var text: String,
    verticalAlignment: VerticalAlignment = VerticalAlignment.CENTER,
    horizontalAlignment: HorizontalAlignment = HorizontalAlignment.CENTER,
    clickUp: (() -> Unit)? = null,
    clickDown: (() -> Unit)? = null
): UIElement(verticalAlignment, horizontalAlignment, clickUp, clickDown) {

    private lateinit var font: BitmapFont
    private lateinit var bounds: Vector2

    init {
        FontManager.applyFontToTarget(this, info.key)
    }

    fun setup(handle: FileHandle) {
        val generator = FreeTypeFontGenerator(handle) // todo move generator to font manager
        val parameter = FreeTypeFontParameter().apply {
            this.size = info.size
            this.color = info.color
        }
        font = generator.generateFont(parameter)

        val layout = GlyphLayout()
        layout.setText(font, text)
        bounds = Vector2(layout.width, layout.height)
        println("Text bounds $bounds")

        generator.dispose()
    }

    override fun getRequestedSize(): Vector2 {
        if (!this::bounds.isInitialized) return Vector2()
        return Vector2(bounds.x / Gdx.graphics.width, bounds.y / Gdx.graphics.height)
    }

    override fun renderToBounds(batch: SpriteBatch) {
        font.draw(batch, text, x * Gdx.graphics.width, y * Gdx.graphics.height)
    }
}
data class FontInfo(val key: String, val size: Int, val color: Color)