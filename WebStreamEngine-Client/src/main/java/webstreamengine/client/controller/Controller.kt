package webstreamengine.client.controller

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils.clamp
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import webstreamengine.client.application.GameInfo
import webstreamengine.client.inputs.InputManager
import webstreamengine.client.managers.SettingsManager

class Controller(
    private var settings: ControllerSettings
) {

    val pcOffsetPosition = Vector3(0f, 0f, 0f)
    val pcOffsetRotation = Vector3(0f, 0f, 0f)
    var pcOffsetRootDistance = 0f

    private var lastMouse: Vector2? = null

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

        // check if the settings allow for the user to rotate the camera, and we currently can rotate around the root
        if (settings.canPlayerChangeRotationAroundRoot && (settings.lockMouse || InputManager.isMouseButtonDown(Input.Buttons.LEFT))) {
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
        var xChange = Gdx.graphics.deltaTime
        var yChange = Gdx.graphics.deltaTime
        when (settings.movementType) {
            ControllerMovementType.NONE -> {
                xChange *= 0f
                yChange *= 0f
            }
            ControllerMovementType.WASD -> {
                xChange *= if (InputManager.isKeyDown(Input.Keys.A)) -settings.movementSpeed else if (InputManager.isKeyDown(Input.Keys.D)) settings.movementSpeed else 0f
                yChange *= if (InputManager.isKeyDown(Input.Keys.S)) settings.movementSpeed else if (InputManager.isKeyDown(Input.Keys.W)) -settings.movementSpeed else 0f
            }
            ControllerMovementType.ARROWS -> {
                xChange *= if (InputManager.isKeyDown(Input.Keys.LEFT)) -settings.movementSpeed else if (InputManager.isKeyDown(Input.Keys.RIGHT)) settings.movementSpeed else 0f
                yChange *= if (InputManager.isKeyDown(Input.Keys.BACK)) settings.movementSpeed else if (InputManager.isKeyDown(Input.Keys.UP)) -settings.movementSpeed else 0f
            }
        }
        if (settings.rootEntity != null) {
            val forward = Vector3(0f, 0f, 1f)
            val right = Vector3(1f, 0f, 0f)
            Quaternion().setEulerAngles(totalRotation.x, 0f, 0f).transform(forward).scl(yChange)
            Quaternion().setEulerAngles(totalRotation.x, 0f, 0f).transform(right).scl(xChange)
            settings.rootEntity!!.move(forward)
            settings.rootEntity!!.move(right)
        }

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

    fun changeSettings(settings: ControllerSettings, resetPlayerControlledOffsets: Boolean = true) {
        this.settings = settings
        if (resetPlayerControlledOffsets) {
            pcOffsetPosition.set(0f, 0f, 0f)
            pcOffsetRotation.set(0f, 0f, 0f)
            pcOffsetRootDistance = 0f
        }
    }
}