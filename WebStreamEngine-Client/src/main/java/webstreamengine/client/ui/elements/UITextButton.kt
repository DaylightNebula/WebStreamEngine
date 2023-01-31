package webstreamengine.client.ui.elements

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import webstreamengine.client.managers.FontManager
import webstreamengine.client.ui.UIBoundedElement
import kotlin.math.roundToInt

class UITextButton(
    private val text: String, fontKey: String,
    xFraction: Float, yFraction: Float,
    widthFraction: Float, val heightFraction: Float,
    val clickCallback: (button: UITextButton) -> Unit
): UIBoundedElement(
    xFraction, yFraction, widthFraction, heightFraction
) {
    init {
        FontManager.applyFontToTarget(this, fontKey)
    }

    fun setup(fileHandle: FileHandle) {
        // generate font
        val generator = FreeTypeFontGenerator(fileHandle)
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = (Gdx.graphics.height * heightFraction).roundToInt()
        val font = generator.generateFont(parameter)

        // setup actor
        actor = TextButton(text, TextButtonStyle().apply { this.font = font })

        // setup click callback
        val me = this
        actor!!.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                clickCallback(me)
            }
        })

        setActorToSourceBounds()
    }
}