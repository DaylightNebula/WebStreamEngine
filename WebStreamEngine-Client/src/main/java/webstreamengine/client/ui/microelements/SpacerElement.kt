package webstreamengine.client.ui.microelements

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import org.json.JSONObject
import webstreamengine.client.ui.HorizontalAlignment
import webstreamengine.client.ui.UIElement
import webstreamengine.client.ui.VerticalAlignment

class SpacerElement(
    id: String,
    private val size: Vector2,
    verticalAlignment: VerticalAlignment = VerticalAlignment.CENTER, horizontalAlignment: HorizontalAlignment = HorizontalAlignment.CENTER
): UIElement(id, verticalAlignment, horizontalAlignment, null, null) {

    constructor(json: JSONObject, id: String, va: VerticalAlignment, ha: HorizontalAlignment): this(
        id,
        Vector2(
            json.optFloat("sizeX", 1f),
            json.optFloat("sizeY", 1f)
        ),
        va, ha
    )

    override fun getRequestedSize(): Vector2 {
        return size
    }

    override fun renderToBounds(batch: SpriteBatch) {}
}