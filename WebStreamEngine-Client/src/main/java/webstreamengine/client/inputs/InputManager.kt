package webstreamengine.client.inputs

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.math.Vector2
import org.json.JSONArray
import org.json.JSONObject
import webstreamengine.client.managers.InputProcessorManager
import java.io.File

object InputManager: InputAdapter() {
    var mouseX: Int = Gdx.input.x
    var mouseY: Int = Gdx.input.y
    var scroll = Vector2()

    private val elements = mutableListOf<InputElement<*>>()
    internal val keysUp = BooleanArray(256) { false }
    internal val keysDown = BooleanArray(256) { false }
    private val mouseButtonsDown = BooleanArray(5) { false }
    private val mouseButtonsUp = BooleanArray(5) { false }

    internal fun isKeyDown(keycode: Int): Boolean { return keysDown[keycode] }
    internal fun isKeyUp(keycode: Int): Boolean { return keysUp[keycode] }
    internal fun isMouseButtonDown(keycode: Int): Boolean { return mouseButtonsDown[keycode] }
    internal fun isMouseButtonUp(keycode: Int): Boolean { return mouseButtonsUp[keycode] }

    private var isSaveDirty = false
    private var jsonFile: File? = null
    private var json: JSONObject? = null

    fun init(jsonFile: File) {
        InputProcessorManager.addProcessor(this)

        // load json file
        this.jsonFile = jsonFile
        if (jsonFile.exists())
            json = JSONObject(jsonFile.readText())
    }

    fun update() {
        // update buttons up arrays
        mouseButtonsUp.forEachIndexed { index, b -> if (b) mouseButtonsUp[index] = false }
        keysUp.forEachIndexed { index, b -> if (b) keysUp[index] = false }
        scroll.set(0f, 0f)

        if (isSaveDirty) {
            isSaveDirty = false
            val json = JSONObject()
            elements.forEach { json.put(it.name, it.toJson()) }

            if (jsonFile?.parentFile?.exists() == false)
                jsonFile?.parentFile?.mkdirs()

            jsonFile?.writeText(json.toString(1))
        }
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        // update mouse trackers
        mouseX = screenX
        mouseY = screenY
        return false
    }
    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        // update mouse button state trackers
        mouseButtonsDown[button] = false
        mouseButtonsUp[button] = true
        return false
    }
    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        // update mouse trackers
        mouseX = screenX
        mouseY = screenY
        return false
    }
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        // update mouse down tracker
        mouseButtonsDown[button] = true
        return false
    }
    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        scroll.set(amountX, amountY)
        return false
    }
    override fun keyUp(keycode: Int): Boolean {
        // update key button state trackers
        keysDown[keycode] = false
        keysUp[keycode] = true
        return false
    }
    override fun keyDown(keycode: Int): Boolean {
        // update key down tracker
        keysDown[keycode] = true
        return false
    }

    fun getElement(name: String): InputElement<*>? {
        return elements.firstOrNull { it.name == name }
    }

    private fun addElement(element: InputElement<*>) {
        // load targets if necessary
        if (json != null && json!!.has(element.name))
            element.loadJson(json!!.getJSONObject(element.name).getJSONArray("targets"))
        elements.add(element)
    }

    fun addAllElements(vararg elements: InputElement<*>) {
        elements.forEach { addElement(it) }
        isSaveDirty = true
    }
}
enum class InputTargetType {
    KEYBOARD,
    MOUSE,
    EMPTY
}
data class InputTarget(
    val targetType: InputTargetType,
    val targetValue: Int
)
abstract class InputElement<T: Any>(val name: String, private val defaultTargets: Array<InputTarget>) {

    var targets = defaultTargets
    fun clearTarget(target: InputTarget) {
        defaultTargets.forEachIndexed { index, _ ->
            if (targets[index] == target)
                targets[index] = InputTarget(InputTargetType.EMPTY, 0)
        }
    }
    fun setTarget(index: Int, target: InputTarget) { targets[index] = target }
    fun setAllTargets(targets: Array<InputTarget>) { this.targets = targets }
    fun reset() { targets = defaultTargets }

    fun loadJson(targetsJsonArray: JSONArray) {
        // the object has already been identified as ours, so we only need to load the targets
        targetsJsonArray.forEachIndexed { idx, json ->
            if (json !is JSONObject) return@forEachIndexed
            targets[idx] = InputTarget(
                InputTargetType.valueOf(json.getString("type")),
                json.getInt("value")
            )
        }
    }

    fun toJson(): JSONObject {
        return JSONObject()
            .put("name", name)
            .put("targets", JSONArray().putAll(
                targets.map { target ->
                    JSONObject()
                        .put("type", target.targetType.name)
                        .put("value", target.targetValue)
                }
            ))
    }

    abstract fun getValue(): T
}
class ButtonDownInputElement(name: String, defaultTargets: Array<InputTarget>): InputElement<Boolean>(name, defaultTargets) {
    override fun getValue(): Boolean {
        // get target
        val target = targets.firstOrNull() ?: return false

        // get result based on target type
        return when(target.targetType) {
            InputTargetType.KEYBOARD -> InputManager.isKeyDown(target.targetValue)
            InputTargetType.MOUSE -> InputManager.isMouseButtonDown(target.targetValue)
            InputTargetType.EMPTY -> false
        }
    }
}
class ButtonUpInputElement(name: String, defaultTargets: Array<InputTarget>): InputElement<Boolean>(name, defaultTargets) {
    override fun getValue(): Boolean {
        // get target
        val target = targets.firstOrNull() ?: return false

        // get result based on target type
        return when(target.targetType) {
            InputTargetType.KEYBOARD -> InputManager.isKeyUp(target.targetValue)
            InputTargetType.MOUSE -> InputManager.isMouseButtonUp(target.targetValue)
            InputTargetType.EMPTY -> false
        }
    }
}
class AxisInputElement(name: String, defaultTargets: Array<InputTarget>): InputElement<Float>(name, defaultTargets) {
    override fun getValue(): Float {
        // make sure we have the appropriate number of default targets
        if (targets.size != 2) throw IllegalArgumentException("AxisInputElement must have only 2 targets")

        // compute the result of both targets
        return -getValueOfTarget(targets.first()) + getValueOfTarget(targets.last())
    }

    private fun getValueOfTarget(target: InputTarget): Float {
        return when(target.targetType) {
            InputTargetType.KEYBOARD -> if (InputManager.isKeyDown(target.targetValue)) 1f else 0f
            InputTargetType.MOUSE -> if (InputManager.isMouseButtonDown(target.targetValue)) 1f else 0f
            InputTargetType.EMPTY -> 0f
        }
    }
}
class StickInputElement(name: String, defaultTargets: Array<InputTarget>): InputElement<Vector2>(name, defaultTargets) {
    override fun getValue(): Vector2 {
        // make sure we have the appropriate number of default targets
        if (targets.size != 4) throw IllegalArgumentException("StickInputElement must have only 4 targets")

        // compute the result of all targets
        return Vector2(
            -getValueOfTarget(targets[0]) + getValueOfTarget(targets[1]),
            -getValueOfTarget(targets[2]) + getValueOfTarget(targets[3])
        )
    }

    private fun getValueOfTarget(target: InputTarget): Float {
        return when(target.targetType) {
            InputTargetType.KEYBOARD -> if (InputManager.isKeyDown(target.targetValue)) 1f else 0f
            InputTargetType.MOUSE -> if (InputManager.isMouseButtonDown(target.targetValue)) 1f else 0f
            InputTargetType.EMPTY -> 0f
        }
    }
}