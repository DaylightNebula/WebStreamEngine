package webstreamengine.client.managers

import org.json.JSONObject
import java.io.File

object SettingsManager {
    private lateinit var jsonFile: File
    private var json: JSONObject? = null
    private var elements = mutableListOf<SettingsElement<*>>()

    private var isDirty = false
    fun markSettingsDirty() { isDirty = true }

    fun update() {
        // make sure the settings are "dirty"
        if (!isDirty) return
        isDirty = false

        // make sure json file exists
        if (!jsonFile.parentFile.exists())
            jsonFile.parentFile.mkdirs()

        // save settings
        val json = JSONObject()
        elements.forEach {
            json.put(it.name, it.getValue())
        }

        // save json
        jsonFile.writeText(json.toString(1))
    }

    fun init(jsonFile: File) {
        // save json file
        this.jsonFile = jsonFile

        // load json if the file exists
        if (jsonFile.exists())
            json = JSONObject(jsonFile.readText())

        // add default elements
        addElement(BooleanSettingsElement("V-Sync", false))
        addElement(BooleanSettingsElement("Anti-aliasing", true))
        addElement(SliderSettingsElement("Look Rate", 20, 5, 200))
        addElement(SliderSettingsElement("Zoom Rate", 20, 5, 100))
    }

    private fun addElement(element: SettingsElement<*>) {
        // if we have a settings json object, load from that
        if (json != null) element.setValue(json!!.get(element.name))

        // save the element
        elements.add(element)
    }

    internal fun addAllElements(vararg element: SettingsElement<*>) {
        // add all the elements
        element.forEach { addElement(it) }

        // mark dirty
        markSettingsDirty()
    }

    fun getElementValue(name: String): Any {
        return elements.firstOrNull { it.name == name }?.getValue()
            ?: throw IllegalArgumentException("No settings element created for name $name")
    }
}
abstract class SettingsElement<T : Any>(val name: String, private val default: T) {
    private var value: Any = default
    abstract fun isValueValid(any: Any): Boolean
    fun reset() { value = default }

    fun setValue(any: Any): Boolean {
        if (!isValueValid(any)) return false
        value = any
        SettingsManager.markSettingsDirty()
        return true
    }
    fun getValue(): T = value as T
}
class BooleanSettingsElement(name: String, default: Boolean): SettingsElement<Boolean>(name, default) {
    override fun isValueValid(any: Any): Boolean = any is Boolean
}
class SliderSettingsElement(name: String, default: Int, private val min: Int, private val max: Int): SettingsElement<Int>(name, default) {
    override fun isValueValid(any: Any): Boolean = (any is Int && any in min..max)
}