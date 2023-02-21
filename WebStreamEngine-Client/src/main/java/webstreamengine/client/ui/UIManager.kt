package webstreamengine.client.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import org.json.JSONArray
import org.json.JSONObject
import webstreamengine.client.JarInterface
import webstreamengine.client.ui.macroelement.ColumnElement
import webstreamengine.client.ui.macroelement.RowElement
import webstreamengine.client.ui.microelements.ImageElement
import webstreamengine.client.ui.microelements.SpacerElement
import webstreamengine.client.ui.microelements.TextElement

object UIManager {

    var isDirty = false
    private var stage = Stage()
    private var scripts = mutableListOf<UserInterface>()

    fun update() {
        if (isDirty) {
            isDirty = false

            scripts.forEach { script ->
                script.elements.forEach {
                    if (it is MacroUIElement)
                        it.updateBounds(0f, 0f, 1f, 1f)
                    else {
                        val size = it.getRequestedSize()
                        it.setPosition(0f, 0f)
                        it.setSize(size.x * Gdx.graphics.width, size.y * Gdx.graphics.height)
                    }
                }
            }
        }
    }

    fun render(batch: SpriteBatch) {
        scripts.forEach { script -> script.elements.forEach { it.renderToBounds(batch) } }
    }

    fun getScripts(): List<UserInterface> {
        return scripts
    }

    fun clearScripts() {
        scripts.clear()
    }

    fun dispose() {
        stage.dispose()
    }

    internal fun addUIScript(script: UserInterface) {
        // get the path to the scripts ui json file
        val targetPath = "uis/${script.path}.json"

        // compile the json files elements into ui elements and pass them to the script
        val jsonArray = JSONArray(JarInterface.getTextResource(targetPath) ?: throw IllegalArgumentException("Could not find $targetPath"))
        script.elements.addAll(jsonArray.map { compileJSONElementToUIElement(it as JSONObject) })

        // make sure the scripts callbacks are registered
        script.registerCallbacks()

        // add the script to the tracking list
        scripts.add(script)

        // mark is dirty
        isDirty = true
    }

    private fun compileJSONElementToUIElement(element: JSONObject): UIElement {
        // load id and type
        val id = element.getString("id") ?: throw IllegalArgumentException("ID not provided in UI json object")
        val type = element.getString("type") ?: throw IllegalArgumentException("Type not provided in UI json object")

        // load elements if we can
        val elements = element.optJSONArray("elements")?.map { compileJSONElementToUIElement(it as? JSONObject ?: throw IllegalArgumentException("")) }

        // load optional alignment
        val vaString = element.optString("verticalAlignment", "center")
        val va = VerticalAlignment.values().firstOrNull { it.name.equals(vaString, true) }
            ?: throw IllegalArgumentException("Vertical alignment $vaString invalid")
        val haString = element.optString("horizontalAlignment", "center")
        val ha = HorizontalAlignment.values().firstOrNull { it.name.equals(haString, true) }
            ?: throw IllegalArgumentException("Horizontal alignment $haString, invalid")

        return when (type) {
            "column" -> ColumnElement(id, elements ?: throw IllegalArgumentException("Elements must be provided for a macro element"), va, ha)
            "row" -> RowElement(id, elements ?: throw IllegalArgumentException("Elements must be provided for a macro element"), va, ha)
            "image" -> ImageElement(element, id, va, ha)
            "spacer" -> SpacerElement(element, id, va, ha)
            "text" -> TextElement(element, id, va, ha)
            else -> throw IllegalArgumentException("Type $type provided in a UI json object is invalid")
        }
    }
}

fun <E> List<E>.sumOf(function: (element: E) -> Float): Float {
    var sum = 0f
    this.forEach { sum += function(it) }
    return sum
}