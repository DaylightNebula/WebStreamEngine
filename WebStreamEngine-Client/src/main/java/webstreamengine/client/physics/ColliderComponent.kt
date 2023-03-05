package webstreamengine.client.physics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.math.Vector3
import webstreamengine.client.Renderer
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.EntityComponent
import webstreamengine.client.networking.NetworkManager
import kotlin.math.abs
import kotlin.math.absoluteValue

class ColliderComponent(
    entity: Entity,
    val box: SimpleBox,
    val isStatic: Boolean,
    val rayCastOnly: Boolean = false
): EntityComponent(entity) {

    val distanceNegative = Vector3()
    val distancePositive = Vector3()

    val min = Vector3()
    val max = Vector3()

    val velocity = Vector3(0f, 0f, 0f)
    private var onGroundScore = 0

    // on start, add to active colliders
    override fun generalStart() {
        if (!NetworkManager.isActive || NetworkManager.isServer)
            PhysicsController.activeColliders.add(this)
    }

    // on stop, remove from active colliders
    override fun generalStop() {
        if (!NetworkManager.isActive || NetworkManager.isServer)
            PhysicsController.activeColliders.remove(this)
    }

    // update velocity and gravity
    override fun clientUpdate() {
        if (isStatic) return

        // apply gravity
        velocity.add(Vector3(PhysicsController.gravity).scl(Renderer.deltaTime))

        // get momentary velocity
        val momentaryVelocity = Vector3(velocity).scl(Renderer.deltaTime)

        // check momentary velocity against movement bounds
        if (momentaryVelocity.x < 0 && distanceNegative.x < momentaryVelocity.x.absoluteValue) {
            velocity.x = 0f
            momentaryVelocity.x = -distanceNegative.x
        } else if (momentaryVelocity.x >= 0 && distancePositive.x < momentaryVelocity.x.absoluteValue) {
            velocity.x = 0f
            momentaryVelocity.x = distancePositive.x
        }
        if (momentaryVelocity.y < 0 && distanceNegative.y < momentaryVelocity.y.absoluteValue) {
            velocity.y = 0f
            momentaryVelocity.y = -distanceNegative.y
        } else if (momentaryVelocity.y >= 0 && distancePositive.y < momentaryVelocity.y.absoluteValue) {
            velocity.y = 0f
            momentaryVelocity.y = distancePositive.y
        }
        if (momentaryVelocity.z < 0 && distanceNegative.z < momentaryVelocity.z.absoluteValue) {
            velocity.z = 0f
            momentaryVelocity.z = -distanceNegative.z
        } else if (momentaryVelocity.z >= 0 && distancePositive.z < momentaryVelocity.z.absoluteValue) {
            velocity.z = 0f
            momentaryVelocity.z = distancePositive.z
        }

        // apply momentary velocity
        entity.setTransformSilent(Vector3(entity.getPosition()).add(momentaryVelocity), entity.getRotation(), entity.getScale())
    }

    fun isMoveValid(move: Vector3): Boolean {
        if (move.x >= 0 && move.x.absoluteValue > distancePositive.x) return false
        if (move.x < 0  && move.x.absoluteValue > distanceNegative.x) return false
        if (move.y >= 0 && move.y.absoluteValue > distancePositive.y) return false
        if (move.y < 0  && move.y.absoluteValue > distanceNegative.y) return false
        if (move.z >= 0 && move.z.absoluteValue > distancePositive.z) return false
        if (move.z < 0  && move.z.absoluteValue > distanceNegative.z) return false
        return true
    }

    override fun serverUpdate() {}
}