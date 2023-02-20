package webstreamengine.client.ui

open class UIScript {
    @UIAction
    fun testClick() {
        println("TEST")
    }
}

annotation class UIAction() {

}