package webstreamengine.client.physics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.Ray
import com.sun.source.util.DocSourcePositions
import webstreamengine.client.entities.Chunk
import webstreamengine.client.entities.EntityChunks
import kotlin.math.abs

object PhysicsController {
    var gravity = -9.81f
    var onGroundDragMult = 0.5f

    fun fakeCastToPlane(ray: Ray, plane: FakeRayCastPlane, level: Float): Vector3 {
        val dstToPlane = plane.dstToFunc(ray.origin, ray.direction, level)
        return Vector3(ray.direction).scl(dstToPlane).add(ray.origin)
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
                println("Colliders found in first")
                colliders = first
                continue
            }

            sourcePosition = Vector3(sourcePosition).add(Vector3(ray.direction).scl(curBoxSize))

            val second = rayCastBox(ray, curBoxSize, sourcePosition)

            if (second.isEmpty()) return null
            println("Colliders found in second")
            colliders = second
        }

        println("Final colliders $colliders")
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
        println("Ray cast box $sourcePosition $masterBoxBounds $pos")
        return getCollidersInBox(pos, masterBox)
    }
    
    fun getCollidersInBox(sourcePosition: Vector3, sourceBox: SimpleBox): List<ColliderComponent> {
        // get chunks to check for collisions
//        FIXME val chunks = EntityChunks.generateChunkPositionList(sourcePosition, sourceBox).mapNotNull { EntityChunks.chunks[it] }
//
//        // get all colliders we collided with, filtered by if we are moving towards them
//        val colliders = mutableListOf<ColliderComponent>()
//        chunks.forEach { chunk ->
//            (chunk.smallEntities + chunk.largeEntities)
//                .forEach { other ->
//                    if (other.getPosition() == sourcePosition) return@forEach
//                    val otherBox = (other.getComponentOfType<ColliderComponent>() ?: return@forEach)
//                    if (otherBox.box.isIntersectingWithOther(other.getPosition(), sourceBox, sourcePosition))
//                        colliders.add(otherBox)
//                }
//        }
//
//        return colliders
        return emptyList()
    }
}
enum class FakeRayCastPlane(val dstToFunc: (vec: Vector3, direction: Vector3, level: Float) -> Float) {
    XZ_PLANE({ vec, dir, level ->
        dir.nor()
        val value = (vec.y - level) / kotlin.math.abs(dir.y)
        println("Value $value ${vec.y} $level ${kotlin.math.abs(dir.y)}")
        value
    }),
    XY_PLANE({ vec, dir, level ->
        kotlin.math.abs(vec.z - level) / kotlin.math.abs(dir.z)
    }),
    YZ_PLANE({ vec, dir, level ->
        kotlin.math.abs(vec.x - level) / kotlin.math.abs(dir.x)
    })
}