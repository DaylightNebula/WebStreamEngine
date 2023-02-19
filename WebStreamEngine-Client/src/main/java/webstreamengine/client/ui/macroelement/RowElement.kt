package webstreamengine.client.ui.macroelement

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import webstreamengine.client.ui.HorizontalAlignment
import webstreamengine.client.ui.MacroUIElement
import webstreamengine.client.ui.UIElement
import webstreamengine.client.ui.VerticalAlignment
import webstreamengine.client.ui.*
import kotlin.math.min

class RowElement(
    elements: Array<UIElement>,
    verticalAlignment: VerticalAlignment,
    horizontalAlignment: HorizontalAlignment,
): MacroUIElement(elements, verticalAlignment, horizontalAlignment) {
    override fun getRequestedSize(): Vector2 {
        val sizes = elements.map { it.getRequestedSize() }
        return Vector2(
            sizes.sumOf { it.x },
            sizes.maxOf { it.y }
        )
    }

    override fun updateBounds(rootX: Float, rootY: Float, rootWidth: Float, rootHeight: Float) {
        // get total size
        val totalSize = getRequestedSize()

        // if total size exceeds max width or height, set an override scale
        val overrideScale = min(1f, min(rootWidth / totalSize.x, rootHeight / totalSize.y))
        if (overrideScale != 1f) {
            totalSize.y = min(1f, totalSize.y)
            totalSize.x = min(1f, totalSize.x)
        }

        // get horizontal base offset (align to left right or center)
        val verticalBaseOffset = when(verticalAlignment) {
            VerticalAlignment.BOTTOM -> rootY
            VerticalAlignment.CENTER -> rootY + (rootHeight / 2f) - (totalSize.y)
            VerticalAlignment.TOP -> rootY + rootHeight - (totalSize.y)
        }

        // get the start vertical offset, elements add to this during bounds updates
        val horizontalExcess = (rootWidth - totalSize.x)
        var horizontalOffset = when(horizontalAlignment) {
            HorizontalAlignment.LEFT -> rootX
            HorizontalAlignment.CENTER -> horizontalExcess / 2f + rootX
            HorizontalAlignment.RIGHT -> horizontalExcess + rootX
        }

        // update all the elements by looping through it backwards (so they appear in the right order)
        elements.forEach { element ->
            val dimensions = element.getRequestedSize()

            // apply override scale if necessary
            if (overrideScale != 1f) {
                dimensions.x *= overrideScale
                dimensions.y *= overrideScale
            }

            // get new position, offset the x position based on the elements horizontal state
            element.setPosition(
                horizontalOffset,

                when(element.verticalAlignment) {
                    VerticalAlignment.TOP -> verticalBaseOffset + (totalSize.y - dimensions.y)
                    VerticalAlignment.CENTER -> verticalBaseOffset + ((totalSize.y - dimensions.y) / 2f)
                    VerticalAlignment.BOTTOM -> verticalBaseOffset
                }
            )

            // keep old size
            element.setSize(dimensions.x, dimensions.y)

            // if the element is a macro element, call its update event
            if (element is MacroUIElement)
                element.updateBounds(element.x, element.y, element.width, element.height)

            // update vertical offset
            horizontalOffset += dimensions.x
        }
    }

    override fun renderToBounds(batch: SpriteBatch) { elements.forEach { it.renderToBounds(batch) } }
}