package webstreamengine.client.ui

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2

abstract class UIElement(
    val id: String,
    val verticalAlignment: VerticalAlignment, val horizontalAlignment: HorizontalAlignment,
    val clickUp: (() -> Unit)?, val clickDown: (() -> Unit)?
) {
    internal var x = 0f
    internal var y = 0f
    internal var width = 0f
    internal var height = 0f

    fun setPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun setSize(width: Float, height: Float) {
        this.width = width
        this.height = height
    }

    abstract fun getRequestedSize(): Vector2
    abstract fun renderToBounds(batch: SpriteBatch)
}
abstract class MacroUIElement(
    id: String, val elements: List<UIElement>, verticalAlignment: VerticalAlignment, horizontalAlignment: HorizontalAlignment
): UIElement(id, verticalAlignment, horizontalAlignment, null, null) {
    abstract fun updateBounds(rootX: Float, rootY: Float, rootWidth: Float, rootHeight: Float)
}
enum class VerticalAlignment() {
    TOP,
    CENTER,
    BOTTOM,
}
enum class HorizontalAlignment() {
    LEFT,
    CENTER,
    RIGHT
}