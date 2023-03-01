package webstreamengine.client.controller

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import webstreamengine.client.Renderer
import webstreamengine.client.controller.ControllerSettings
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.EntityComponent
import webstreamengine.client.inputs.InputManager
import webstreamengine.client.inputs.StickInputElement
import webstreamengine.client.managers.SettingsManager
import webstreamengine.client.networking.NetworkManager
import webstreamengine.client.physics.ColliderComponent

class PlayerControllerComponent(
    entity: Entity,
    
    // flags
    private val canPlayerZoom: Boolean,
    private val canPlayerRotate: Boolean,
    private val lockMouse: Boolean,
    private val moveInputStick: String?,
    
    // settings
    private val defaultRotation: Vector3,
    private val defaultDistanceFromRoot: Float,
    var movementSpeed: Float,

    // preset settings
    private val minDistanceFromRoot: Float = 0f,
    private val maxDistanceFromRoot: Float = defaultDistanceFromRoot,
    private val positionOffset: Vector3 = Vector3(0f, 0f, 0f)
): EntityComponent(entity) {

    private val pcOffsetPosition = Vector3(0f, 0f, 0f)
    private val pcOffsetRotation = Vector3(0f, 0f, 0f)
    private var pcOffsetRootDistance = 0f

    private var lastMouse: Vector2? = null
    private var drag = lockMouse

    var firstclientupdate = true
    override fun clientUpdate() {
        if (NetworkManager.isActive && NetworkManager.myID != entity.assignedTo) return

        if (firstclientupdate) {
            firstclientupdate = false
            Renderer.setCursorCatched(lockMouse)
        }

        if (canPlayerZoom) updateDistanceFromRoot()
        if (canPlayerRotate) updateLookRotation()
        if (moveInputStick != null) updateMovement()
        updateCamera()
    }

    private fun updateDistanceFromRoot() {
        // get the new distance by clamping the new value between the min and max adjusted by the default
        val scrollSensitivity = (SettingsManager.getElementValue("Zoom Rate") as Int).toFloat()
        pcOffsetRootDistance = MathUtils.clamp(
            pcOffsetRootDistance + InputManager.scroll.y * Gdx.graphics.deltaTime * scrollSensitivity,
            minDistanceFromRoot - defaultDistanceFromRoot,
            maxDistanceFromRoot - defaultDistanceFromRoot
        )
    }

    private fun updateLookRotation() {
        if (!lockMouse && InputManager.isMouseButtonDown(Input.Buttons.RIGHT))
            drag = true
        else if (!lockMouse && InputManager.isMouseButtonUp(Input.Buttons.RIGHT)) {
            drag = false
            lastMouse = null
        }

        if (lastMouse != null && drag) {
            val mouseSensitivity = (SettingsManager.getElementValue("Look Rate") as Int).toFloat()
            val diffX = InputManager.mouseX.toFloat() - lastMouse!!.x
            val diffY = InputManager.mouseY.toFloat() - lastMouse!!.y
            val newX = pcOffsetRotation.x + (-diffX * mouseSensitivity * Gdx.graphics.deltaTime)
            val newY =
                MathUtils.clamp(pcOffsetRotation.y + (-diffY * mouseSensitivity * Gdx.graphics.deltaTime), -75f, 75f)
            pcOffsetRotation.set(newX, newY, 0f)
        }
        lastMouse = Vector2(InputManager.mouseX.toFloat(), InputManager.mouseY.toFloat())
    }

    private fun updateMovement() {
        // get total rotation
        val rotation = Vector3(
            defaultRotation.x + pcOffsetRotation.x,
            defaultRotation.y + pcOffsetRotation.y,
            defaultRotation.z + pcOffsetRotation.z
        )

        // get input stick
        val stick = InputManager.getElement(moveInputStick!!) as? StickInputElement ?: return

        // get total change scaled by the current delta time
        val change = stick.getValue()

        // get collider
        val collider = entity.getComponentOfType<ColliderComponent>()

        // do movement
        val forward = Vector3(0f, 0f, movementSpeed)
        val right = Vector3(movementSpeed, 0f, 0f)
        Quaternion().setEulerAngles(rotation.x, 0f, 0f).transform(forward).scl(change.y)
        Quaternion().setEulerAngles(rotation.x, 0f, 0f).transform(right).scl(change.x)
        val move = Vector3(forward).add(right).scl(Gdx.graphics.deltaTime)

        // if we found a collider, ask it if we can move, if not, cancel
        if (collider != null && !collider.isMoveValid(move)) return

        // do move if we made it this far
        entity.move(move)
    }

    private fun updateCamera() {
        // get root location
        val root = Vector3(entity.getPosition())

        // get cameras direction from root
        val totalRotation = Vector3(
            defaultRotation.x + pcOffsetRotation.x,
            defaultRotation.y + pcOffsetRotation.y,
            defaultRotation.z + pcOffsetRotation.z
        )
        val direction = Vector3(0f, 0f, 1f)
        Quaternion().setEulerAngles(totalRotation.x, totalRotation.y, totalRotation.z).transform(direction)

        // multiply the vector by the total distance from the root
        direction.x *= defaultDistanceFromRoot + pcOffsetRootDistance
        direction.y *= defaultDistanceFromRoot + pcOffsetRootDistance
        direction.z *= defaultDistanceFromRoot + pcOffsetRootDistance

        // update location
        val location = Vector3(root)
        location.add(direction)
        location.add(pcOffsetPosition)
        location.add(positionOffset)
        Renderer.cam.position.set(location)

        // update look
        Renderer.cam.lookAt(root)
        Renderer.cam.up.set(0f, 1f, 0f)
        Renderer.cam.update()
    }

    override fun generalStart() {}
    override fun generalStop() {}
    override fun serverUpdate() {}
}