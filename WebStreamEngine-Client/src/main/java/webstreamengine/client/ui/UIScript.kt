package webstreamengine.client.ui

abstract class UIScript(val path: String) {
    val elements = mutableListOf<UIElement>()
    private val callbacks = hashMapOf<TargetElement, () -> Unit>()

    fun runCallback(id: String, type: InteractType) {
        callbacks[TargetElement(id, type)]?.let { it() }
    }

    fun registerCallback(target: TargetElement, callback: () -> Unit) {
        callbacks[target] = callback
    }

    abstract fun registerCallbacks()
}
data class TargetElement(val id: String, val type: InteractType)
enum class InteractType {
    UP,
    DOWN
}