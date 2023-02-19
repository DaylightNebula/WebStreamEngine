package webstreamengine.client.ui

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage

object UIManager {

    var isDirty = false
    private var stage = Stage()
    private var elements = mutableListOf<MacroUIElement>()

    fun addElement(element: MacroUIElement) {
        elements.add(element)
        isDirty = true
    }

    fun update() {
        if (isDirty) {
            isDirty = false

            elements.forEach {
                it.updateBounds(0f, 0f, 1f, 1f)
            }
        }
    }

    fun render(batch: SpriteBatch) {
        elements.forEach { it.renderToBounds(batch) }
    }

    fun getElements(): List<UIElement> {
        return elements
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