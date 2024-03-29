package webstreamengine.client.application

import webstreamengine.client.inputs.InputElement
import webstreamengine.client.managers.SettingsElement

abstract class Application {
    abstract fun start()
    abstract fun update()
    abstract fun stop()
    abstract fun getSettings(): Array<SettingsElement<*>>
    open fun getBaseHTTPParams(): Array<Pair<String, Any?>> {
        return emptyArray()
    }
}