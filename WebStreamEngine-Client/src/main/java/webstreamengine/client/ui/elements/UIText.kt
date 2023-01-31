package webstreamengine.client.ui.elements

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import webstreamengine.client.managers.FontManager
import webstreamengine.client.ui.UIElement
import kotlin.math.roundToInt

class UIText(
    private val text: String, fontKey: String,
    private val xFraction: Float, private val yFraction: Float,
    private val wFraction: Float, private val hFraction: Float,
    private val align: Int = Align.center
): UIElement() {
    companion object {
        fun buildStyle(font: BitmapFont, color: Color = Color.WHITE): LabelStyle {
            return LabelStyle(font, color)
        }
    }

    init {
        FontManager.applyFontToTarget(this, fontKey)
    }

    fun setup(fontHandle: FileHandle) {
        // calculate bounds
        val x = Gdx.graphics.width * xFraction
        val y = Gdx.graphics.height * yFraction
        val width = Gdx.graphics.width * wFraction
        val height = Gdx.graphics.height * hFraction

        // generate font
        val generator = FreeTypeFontGenerator(fontHandle)
        val parameter = FreeTypeFontParameter()
        parameter.size = height.roundToInt()
        val font = generator.generateFont(parameter)

        // setup label with alignment
        val label = Label(text, buildStyle(font))
        label.setBounds(x, y, width, height)
        label.setAlignment(align)
        label.debug = true
        actor = label

        // setup width
        if (width > label.prefWidth) {
            label.setFontScale(width / label.prefWidth)
        }
    }
}