package webstreamengine.client.ui.elements

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import webstreamengine.client.ui.UIBoundedElement

class UITextButton(
    text: String, style: TextButtonStyle,
    xFraction: Float, yFraction: Float,
    widthFraction: Float, heightFraction: Float,
    val clickCallback: (button: UITextButton) -> Unit
): UIBoundedElement(
    xFraction, yFraction, widthFraction, heightFraction
) {
    init {
        // setup actor
        actor = TextButton(text, style)

        // setup click callback
        val me = this
        actor!!.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                clickCallback(me)
            }
        })
    }
}