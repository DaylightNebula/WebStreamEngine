package webstreamengine.test

import webstreamengine.client.ui.InteractType
import webstreamengine.client.ui.TargetElement
import webstreamengine.client.ui.UserInterface

class TestUI: UserInterface("test_ui") {
    override fun registerCallbacks() {
        registerCallback(TargetElement("play_button", InteractType.UP)) { println("PLAY") }
        registerCallback(TargetElement("quit_button", InteractType.UP)) { println("QUIT") }
    }
}