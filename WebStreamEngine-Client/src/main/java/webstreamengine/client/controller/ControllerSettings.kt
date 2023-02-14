package webstreamengine.client.controller

import com.badlogic.gdx.math.Vector3
import webstreamengine.client.entities.Entity

data class ControllerSettings(
    // root info
    val rootEntity: Entity?,
    val backupRootLocation: Vector3,
    val offsetFromRoot: Vector3,

    // movement info
    val movementType: ControllerMovementType,
    val movementSpeed: Float,
    val shouldMovementRespectPhysics: Boolean,
    // todo values to control player entity acceleration

    // distance from root info
    val defaultDistanceFromRoot: Float,
    val distanceFromRootMinMax: Pair<Float, Float>,
    val canPlayerChangeDistanceFromRoot: Boolean,

    // rotation around root info
    val lockMouse: Boolean,
    val defaultRotationAroundRoot: Vector3,
    val canPlayerChangeRotationAroundRoot: Boolean,
) {
    companion object {
        fun genStaticSettings(): ControllerSettings {
            return ControllerSettings(
                rootEntity = null,
                backupRootLocation = Vector3(0f, 0f, 0f),
                offsetFromRoot = Vector3(0f, 0f, 0f),
                movementType = ControllerMovementType.NONE,
                movementSpeed = 0f,
                shouldMovementRespectPhysics = false,
                defaultDistanceFromRoot = 0f,
                distanceFromRootMinMax = Pair(0f, 0f),
                canPlayerChangeDistanceFromRoot = false,
                defaultRotationAroundRoot = Vector3(0f, 0f, 0f),
                canPlayerChangeRotationAroundRoot = false,
                lockMouse = false
            )
        }

        fun genThirdPersonSettings(entity: Entity, movespeed: Float, rotation: Vector3, dstToRoot: Float): ControllerSettings {
            return ControllerSettings(
                rootEntity = entity,
                backupRootLocation = Vector3(0f, 0f, 0f),
                offsetFromRoot = Vector3(0f, 0f, 0f),
                movementType = ControllerMovementType.WASD,
                movementSpeed = movespeed,
                shouldMovementRespectPhysics = true,
                defaultDistanceFromRoot = dstToRoot,
                distanceFromRootMinMax = Pair(1f, dstToRoot),
                canPlayerChangeDistanceFromRoot = false,
                defaultRotationAroundRoot = rotation,
                canPlayerChangeRotationAroundRoot = true,
                lockMouse = true
            )
        }
    }
}
enum class ControllerMovementType {
    WASD,
    ARROWS,
    NONE
}