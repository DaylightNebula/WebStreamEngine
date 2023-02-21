package webstreamengine.client.ui

abstract class UserInterface(val path: String) {
    val elements = mutableListOf<UIElement>()
    private val callbacks = hashMapOf<TargetElement, () -> Unit>()

    fun runCallback(id: String, type: InteractType) {
        callbacks[TargetElement(id, type)]?.let { it() }
    }

    fun registerCallback(target: TargetElement, callback: () -> Unit) {
        callbacks[target] = callback
    }

    abstract fun registerCallbacks()

    companion object {
        private val constructors = hashMapOf<String, () -> UserInterface>()
        fun registerInterface(id: String, c: () -> UserInterface) { constructors[id] = c }
        fun loadInterface(id: String) {
            if (!constructors.containsKey(id)) throw IllegalArgumentException("No user interface registered with id $id")
            UIManager.addUIScript(constructors[id]!!())
        }
    }
}
data class TargetElement(val id: String, val type: InteractType)
enum class InteractType {
    UP,
    DOWN
}
object UIScriptManager {

}