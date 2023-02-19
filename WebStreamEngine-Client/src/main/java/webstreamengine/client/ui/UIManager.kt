package webstreamengine.client.ui

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage

object UIManager {

    var isDirty = false
    private var stage = Stage()
    private var elements = mutableListOf<UIElement>()

    fun addElement(element: UIElement) {
        elements.add(element)
        isDirty = true
    }

    fun update() {
        if (isDirty) {
            isDirty = false

            elements.forEach {
                if (it is MacroUIElement)
                    it.updateBounds(0f, 0f, 1f, 1f)
                else {
                    val dimensions = it.getRequestedSize()
                    it.setSize(dimensions.x, dimensions.y)
                    it.setPosition(
                        0.5f - (dimensions.x * 0.5f),
                        0.5f - (dimensions.y * 0.5f)
                    )
                }
            }
        }
    }

    fun render(batch: SpriteBatch) {
        elements.forEach { it.renderToBounds(batch) }
    }

    fun dispose() {
        stage.dispose()
    }
}

fun <E> List<E>.sumOf(function: (element: E) -> Float): Float {
    var sum = 0f
    this.forEach { sum += function(it) }
    return sum
}