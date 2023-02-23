package webstreamengine.client.controller

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils.clamp
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import webstreamengine.client.application.GameInfo
import webstreamengine.client.inputs.InputManager
import webstreamengine.client.inputs.StickInputElement
import webstreamengine.client.managers.SettingsManager
import webstreamengine.client.physics.ColliderComponent

class Controller(
    private var settings: ControllerSettings
) {

    val pcOffsetPosition = Vector3(0f, 0f, 0f)
    val pcOffsetRotation = Vector3(0f, 0f, 0f)
    var pcOffsetRootDistance = 0f

    private var lastMouse: Vector2? = null
    private var drag = settings.lockMouse

    fun update() {
        // if the settings have scroll wheel enabled, apply necessary updates
        if (settings.canPlayerChangeDistanceFromRoot) {
            // get the new distance by clamping the new value between the min and max adjusted by the default
            val scrollSensitivity = SettingsManager.getElementValue("Zoom Rate") as Float
            pcOffsetRootDistance = clamp(
                pcOffsetRootDistance + InputManager.scroll.y * Gdx.graphics.deltaTime * scrollSensitivity,
                settings.distanceFromRootMinMax.first - settings.defaultDistanceFromRoot,
                settings.distanceFromRootMinMax.second - settings.defaultDistanceFromRoot
            )
        }

        // update cursor lock
        Gdx.input.isCursorCatched = settings.lockMouse

        if (!settings.lockMouse && InputManager.isMouseButtonDown(Input.Buttons.RIGHT))
            drag = true
        else if (!settings.lockMouse && InputManager.isMouseButtonUp(Input.Buttons.RIGHT)) {
            drag = false
            lastMouse = null
        }

        // check if the settings allow for the user to rotate the camera, and we currently can rotate around the root
        if (settings.canPlayerChangeRotationAroundRoot && drag) {
            if (lastMouse != null) {
                val mouseSensitivity = (SettingsManager.getElementValue("Look Rate") as Int).toFloat()
                val diffX = InputManager.mouseX.toFloat() - lastMouse!!.x
                val diffY = InputManager.mouseY.toFloat() - lastMouse!!.y
                val newX = pcOffsetRotation.x + (-diffX * mouseSensitivity * Gdx.graphics.deltaTime)
                val newY = clamp(pcOffsetRotation.y + (-diffY * mouseSensitivity * Gdx.graphics.deltaTime), -75f, 75f)
                pcOffsetRotation.set(newX, newY, 0f)
            }
            lastMouse = Vector2(InputManager.mouseX.toFloat(), InputManager.mouseY.toFloat())
        }

        // get calculation info
        val camera = GameInfo.cam
        val root = settings.rootEntity?.getPosition() ?: settings.backupRootLocation

        // convert total rotation to a direction vector
        val totalRotation = Vector3(
            settings.defaultRotationAroundRoot.x + pcOffsetRotation.x,
            settings.defaultRotationAroundRoot.y + pcOffsetRotation.y,
            settings.defaultRotationAroundRoot.z + pcOffsetRotation.z
        )
        val direction = Vector3(0f, 0f, 1f)
        Quaternion().setEulerAngles(totalRotation.x, totalRotation.y, totalRotation.z).transform(direction)

        // multiply the vector by the total distance from the root
        direction.x *= settings.defaultDistanceFromRoot + pcOffsetRootDistance
        direction.y *= settings.defaultDistanceFromRoot + pcOffsetRootDistance
        direction.z *= settings.defaultDistanceFromRoot + pcOffsetRootDistance

        // update movement
        updateMovement(totalRotation)

        // set cameras location based of direction and position offsets
        val location = Vector3(root)
        location.add(direction)
        location.add(pcOffsetPosition)
        location.add(settings.offsetFromRoot)
        camera.position.set(location)

        // tell camera to look at the root location
        camera.lookAt(root)
        camera.up.set(0f, 1f, 0f)

        // update the camera
        camera.update()
    }

    fun updateMovement(rotation: Vector3) {
        // get stick
        if (settings.movementStickInputName == null || settings.rootEntity == null) return
        val stick = InputManager.getElement(settings.movementStickInputName!!) as? StickInputElement ?: return

        // get total change scaled by the current delta time
        val change = stick.getValue()

        val collider = settings.rootEntity?.getComponentOfType<ColliderComponent>()

        val forward = Vector3(0f, 0f, settings.movementSpeed)
        val right = Vector3(settings.movementSpeed, 0f, 0f)
        Quaternion().setEulerAngles(rotation.x, 0f, 0f).transform(forward).scl(change.y)
        Quaternion().setEulerAngles(rotation.x, 0f, 0f).transform(right).scl(change.x)
        val move = Vector3(forward).add(right).scl(Gdx.graphics.deltaTime)

        if (collider != null && !collider.isMoveValid(move)) return

        settings.rootEntity!!.move(move)
    }

    fun changeSettings(settings: ControllerSettings, resetPlayerControlledOffsets: Boolean = true) {
        this.settings = settings
        if (resetPlayerControlledOffsets) {
            pcOffsetPosition.set(0f, 0f, 0f)
            pcOffsetRotation.set(0f, 0f, 0f)
            pcOffsetRootDistance = 0f
        }
    }
}