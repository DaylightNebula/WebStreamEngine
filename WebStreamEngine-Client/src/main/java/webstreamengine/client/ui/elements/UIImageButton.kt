package webstreamengine.client.ui.elements

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import webstreamengine.client.ui.UIBoundedElement
import webstreamengine.client.ui.UIElement

class UIImageButton(
    textureID: String,
    xFraction: Float, yFraction: Float,
    widthFraction: Float, heightFraction: Float,
    val clickCallback: (button: UIImageButton) -> Unit
): UIBoundedElement(
    ImageButton(
        TextureRegionDrawable(
            Texture(
                Gdx.files.internal("assets/$textureID.png") // todo load from server
            )
        )
    ),
    xFraction, yFraction, widthFraction, heightFraction
) {
    init {
        // add proper callback to the actor
        val me = this
        actor.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                clickCallback(me)
            }
        })
    }
}