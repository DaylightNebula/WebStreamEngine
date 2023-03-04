package webstreamengine.client.ui.macroelement

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import webstreamengine.client.ui.*
import kotlin.math.min

class ColumnElement(
    id: String,
    elements: List<UIElement>,
    verticalAlignment: VerticalAlignment,
    horizontalAlignment: HorizontalAlignment
): MacroUIElement(
    id, elements, verticalAlignment, horizontalAlignment
) {
    override fun getRequestedSize(): Vector2 {
        val sizes = elements.map { it.getRequestedSize() }
        return Vector2(
            sizes.maxOf { it.x },
            sizes.sumOf { it.y }
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
        val horizontalBaseOffset = when(horizontalAlignment) {
            HorizontalAlignment.LEFT -> rootX + (totalSize.x / 2f)
            HorizontalAlignment.CENTER -> rootX + (rootWidth / 2f)
            HorizontalAlignment.RIGHT -> rootX + rootWidth - (totalSize.x / 2f)
        }

        // get the start vertical offset, elements add to this during bounds updates
        val verticalExcess = (rootHeight - totalSize.y)
        var verticalOffset = when(verticalAlignment) {
            VerticalAlignment.BOTTOM -> rootY
            VerticalAlignment.CENTER -> verticalExcess / 2f + rootY
            VerticalAlignment.TOP -> verticalExcess + rootY
        }

        // update all the elements by looping through it backwards (so they appear in the right order)
        elements.reversed().forEach { element ->
            val dimensions = element.getRequestedSize()
            println("Dimensions $dimensions")

            // apply override scale if necessary
            if (overrideScale != 1f) {
                dimensions.x *= overrideScale
                dimensions.y *= overrideScale
            }

            // get new position, offset the x position based on the elements horizontal state
            element.setPosition(
                when(element.horizontalAlignment) {
                    HorizontalAlignment.LEFT -> horizontalBaseOffset - dimensions.x
                    HorizontalAlignment.CENTER -> horizontalBaseOffset - (dimensions.x / 2f)
                    HorizontalAlignment.RIGHT -> horizontalBaseOffset
                },
                verticalOffset
            )

            // keep old size
            element.setSize(dimensions.x, dimensions.y)

            // if the element is a macro element, call its update event
            if (element is MacroUIElement)
                element.updateBounds(element.x, element.y, element.width, element.height)

            // update vertical offset
            verticalOffset += dimensions.y
        }
    }

    override fun renderToBounds(batch: SpriteBatch) { elements.forEach { it.renderToBounds(batch) } }
}
