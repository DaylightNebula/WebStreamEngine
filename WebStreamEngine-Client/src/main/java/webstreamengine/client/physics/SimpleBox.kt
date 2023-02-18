package webstreamengine.client.physics

import com.badlogic.gdx.math.Vector3
import kotlin.math.abs

data class SimpleBox(val center: Vector3, val bounds: Vector3) {
    companion object {
        fun checkBoxIntersection(source: SimpleBox, sourceOffset: Vector3, other: SimpleBox, otherOffset: Vector3): Boolean {
            val so2 = Vector3(sourceOffset).add(source.center)
            val oo2 = Vector3(otherOffset).add(other.center)

            return abs(so2.x - oo2.x) < abs((source.bounds.x + other.bounds.x) / 2) &&
                    abs(so2.y - oo2.y) < abs((source.bounds.y + other.bounds.y) / 2) &&
                    abs(so2.z - oo2.z) < abs((source.bounds.z + other.bounds.z) / 2)
        }


    }
}