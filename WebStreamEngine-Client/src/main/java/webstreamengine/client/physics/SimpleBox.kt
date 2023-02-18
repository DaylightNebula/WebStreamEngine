package webstreamengine.client.physics

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import kotlin.math.abs

data class SimpleBox(val center: Vector3, val bounds: Vector3) {
    companion object {
        fun checkBoxIntersection(source: SimpleBox, sourceOffset: Vector3, other: SimpleBox, otherOffset: Vector3): Boolean {
//            val sBox = BoundingBox(
//                Vector3(source.bounds).scl(-0.5f).add(source.center).add(sourceOffset),
//                Vector3(source.bounds).scl(0.5f).add(source.center).add(sourceOffset)
//            )
//            val oBox = BoundingBox(
//                Vector3(other.bounds).scl(-0.5f).add(other.center).add(otherOffset),
//                Vector3(other.bounds).scl(0.5f).add(other.center).add(otherOffset)
//            )
//            return oBox.intersects(sBox)
            val so2 = Vector3(sourceOffset).add(source.center)
            val oo2 = Vector3(otherOffset).add(other.center)

            val xDist = abs(so2.x - oo2.x)
            val yDist = abs(so2.y - oo2.y)
            val zDist = abs(so2.z - oo2.z)
            val minXDist = (source.bounds.x / 2) + (other.bounds.x / 2)
            val minYDist = (source.bounds.y / 2) + (other.bounds.y / 2)
            val minZDist = (source.bounds.z / 2) + (other.bounds.z / 2)
            return xDist < minXDist && yDist < minYDist && zDist < minZDist

//            return abs(so2.x - oo2.x) > abs((source.bounds.x + other.bounds.x) / 2) &&
//                    abs(so2.y - oo2.y) > abs((source.bounds.y + other.bounds.y) / 2) &&
//                    abs(so2.z - oo2.z) > abs((source.bounds.z + other.bounds.z) / 2)
        }
    }

    fun isIntersectingWithOther(offset: Vector3, other: SimpleBox, otherOffset: Vector3): Boolean {
        return checkBoxIntersection(this, offset, other, otherOffset)
    }
}