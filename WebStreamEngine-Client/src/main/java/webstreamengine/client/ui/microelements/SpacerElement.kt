package webstreamengine.client.ui.microelements

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import webstreamengine.client.ui.HorizontalAlignment
import webstreamengine.client.ui.UIElement
import webstreamengine.client.ui.VerticalAlignment

class SpacerElement(
    private val size: Vector2,
    verticalAlignment: VerticalAlignment = VerticalAlignment.CENTER, horizontalAlignment: HorizontalAlignment = HorizontalAlignment.CENTER
): UIElement(verticalAlignment, horizontalAlignment, null, null) {
    override fun getRequestedSize(): Vector2 {
        return size
    }

    override fun renderToBounds(batch: SpriteBatch) {}
}