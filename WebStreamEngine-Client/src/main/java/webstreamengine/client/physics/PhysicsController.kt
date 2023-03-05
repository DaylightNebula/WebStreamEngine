package webstreamengine.client.physics

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.Ray
import webstreamengine.client.entities.EntityHandler
import kotlin.math.abs

object PhysicsController {
    val gravity = Vector3(0f, -9.81f, 0f)
    val activeColliders = mutableListOf<ColliderComponent>()

    fun update() {
        activeColliders.forEach { collider ->
            collider.distanceNegative.x = Float.POSITIVE_INFINITY
            collider.distanceNegative.y = Float.POSITIVE_INFINITY
            collider.distanceNegative.z = Float.POSITIVE_INFINITY
            collider.distancePositive.x = Float.POSITIVE_INFINITY
            collider.distancePositive.y = Float.POSITIVE_INFINITY
            collider.distancePositive.z = Float.POSITIVE_INFINITY
        }

        // loop through all colliders to render each one
        activeColliders.take(activeColliders.size).forEachIndexed { idx, collider ->
            // get min and max positions of the collider
            val min = Vector3(collider.entity.getPosition()).add(collider.box.center).sub(Vector3(collider.box.bounds).scl(0.5f))
            val max = Vector3(collider.entity.getPosition()).add(collider.box.center).sub(Vector3(collider.box.bounds).scl(0.5f))
            collider.min.set(min)
            collider.max.set(max)

            // if index 0, just set max distances to infinity and cancel
            if (idx == 0) {
                return@forEachIndexed
            }

            // loop through all previous colliders
            activeColliders.take(idx).forEach { other ->
                val oMin = other.min
                val oMax = other.max

                val xIntersect =  min.x <= oMax.x && oMin.x <= max.x
                val yIntersect =  min.y <= oMax.y && oMin.y <= max.y
                val zIntersect = min.z <= oMax.z && oMin.z <= max.z

                // check if collider overlaps on the y-axis
                if (zIntersect && yIntersect) {
                    val dstOriginal = max.x - oMin.x
                    if (dstOriginal >= 0) {
                        val finalDst = dstOriginal - (collider.box.bounds.x / 2f) - (other.box.bounds.x / 2f)
                        if (collider.distanceNegative.x > finalDst) { collider.distanceNegative.x = finalDst }
                        if (other.distancePositive.x > finalDst) { other.distancePositive.x = finalDst }
                    } else {
                        val finalDst = -dstOriginal - (collider.box.bounds.x / 2f) - (other.box.bounds.x / 2f)
                        if (collider.distancePositive.x > finalDst) { collider.distancePositive.x = finalDst }
                        if (other.distanceNegative.x > finalDst) { other.distanceNegative.x = finalDst }
                    }
                }
                else if (zIntersect && xIntersect) {
                    val dstOriginal = max.y - oMin.y
                    if (dstOriginal >= 0) {
                        val finalDst = dstOriginal - (collider.box.bounds.x / 2f) - (other.box.bounds.x / 2f)
                        if (collider.distanceNegative.y > finalDst) { collider.distanceNegative.y = finalDst }
                        if (other.distancePositive.y > finalDst) { other.distancePositive.y = finalDst }
                    } else {
                        val finalDst = -dstOriginal - (collider.box.bounds.x / 2f) - (other.box.bounds.x / 2f)
                        if (collider.distancePositive.y > finalDst) { collider.distancePositive.y = finalDst }
                        if (other.distanceNegative.y > finalDst) { other.distanceNegative.y = finalDst }
                    }
                }
                else if (xIntersect && yIntersect) {
                    val dstOriginal = max.z - oMin.z
                    if (dstOriginal >= 0) {
                        val finalDst = dstOriginal - (collider.box.bounds.x / 2f) - (other.box.bounds.x / 2f)
                        if (collider.distanceNegative.z > finalDst) { collider.distanceNegative.z = finalDst }
                        if (other.distancePositive.z > finalDst) { other.distancePositive.z = finalDst }
                    } else {
                        val finalDst = -dstOriginal - (collider.box.bounds.x / 2f) - (other.box.bounds.x / 2f)
                        if (collider.distancePositive.z > finalDst) { collider.distancePositive.z = finalDst }
                        if (other.distanceNegative.z > finalDst) { other.distanceNegative.z = finalDst }
                    }
                }
            }
        }
    }

    fun rayCast(ray: Ray, length: Float = 100f, percision: Float = .1f): Pair<ColliderComponent, Vector3>? {
        var curBoxSize = length
        var sourcePosition = ray.origin
        var colliders: List<ColliderComponent>? = null
        ray.direction.nor()
        println("Ray ${ray.origin} ${ray.direction}")

        while (curBoxSize > percision) {
            curBoxSize /= 2f

            val first = rayCastBox(ray, curBoxSize, sourcePosition)

            if (first.isNotEmpty()) {
                colliders = first
                continue
            }

            sourcePosition = Vector3(sourcePosition).add(Vector3(ray.direction).scl(curBoxSize))

            val second = rayCastBox(ray, curBoxSize, sourcePosition)

            if (second.isEmpty()) return null
            colliders = second
        }

        if (colliders.isNullOrEmpty()) return null

        return Pair(colliders.first(), sourcePosition)
    }

    private fun rayCastBox(ray: Ray, rayLength: Float, sourcePosition: Vector3): List<ColliderComponent> {
        val masterBoxBounds = Vector3(ray.direction).scl(rayLength)
        val masterBox = SimpleBox(
            Vector3.Zero,
            Vector3(abs(ray.direction.x), abs(ray.direction.y), abs(ray.direction.z)).scl(rayLength)
        )
        val pos = Vector3(sourcePosition).add(Vector3(masterBoxBounds).scl(0.5f))
        return getCollidersInBox(pos, masterBox)
    }

    fun getCollidersInBox(sourcePosition: Vector3, sourceBox: SimpleBox): List<ColliderComponent> {
        // get all colliders we collided with, filtered by if we are moving towards them
        val colliders = mutableListOf<ColliderComponent>()
        EntityHandler.entities.forEach { other ->
                    if (other.getPosition() == sourcePosition) return@forEach
                    val otherBox = (other.getComponentOfType<ColliderComponent>() ?: return@forEach)
                    if (otherBox.box.isIntersectingWithOther(other.getPosition(), sourceBox, sourcePosition))
                        colliders.add(otherBox)
                }

        return colliders
    }
}