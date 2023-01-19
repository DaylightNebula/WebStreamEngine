package webstreamengine.client.inputs

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputProcessor
import webstreamengine.client.managers.InputProcessorManager

object InputManager: InputAdapter() {
    var mouseX: Int = Gdx.input.x
    var mouseY: Int = Gdx.input.y

    val mouseButtonsDown = BooleanArray(5) { false }
    val mouseButtonsUp = BooleanArray(5) { false }
    val keysDown = BooleanArray(255) { false }
    val keysUp = BooleanArray(255) { false }

    fun isKeyDown(keycode: Int): Boolean { return keysDown[keycode] }
    fun isKeyUp(keycode: Int): Boolean { return keysUp[keycode] }
    fun isMouseButtonDown(keycode: Int): Boolean { return mouseButtonsDown[keycode] }
    fun isMouseButtonUp(keycode: Int): Boolean { return mouseButtonsUp[keycode] }

    fun init() {
        InputProcessorManager.addProcessor(this)
    }

    fun update() {
        // update buttons up arrays
        mouseButtonsUp.forEachIndexed { index, b -> if (b) mouseButtonsUp[index] = false }
        keysUp.forEachIndexed { index, b -> if (b) keysUp[index] = false }
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        // update mouse trackers
        mouseX = screenX
        mouseY = screenY
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        // update mouse button state trackers
        mouseButtonsDown[button] = false
        mouseButtonsUp[button] = true
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        // update mouse down tracker
        mouseButtonsDown[button] = true
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        // update key button state trackers
        keysDown[keycode] = false
        keysUp[keycode] = true
        return true
    }

    override fun keyDown(keycode: Int): Boolean {
        // update key down tracker
        keysDown[keycode] = true
        return true
    }
}