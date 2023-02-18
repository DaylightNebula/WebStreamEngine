package webstreamengine.client.physics

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.Ray

object PhysicsController {
    var gravity = -9.81f
    var onGroundDragMult = 0.5f

    fun fakeCastToPlane(ray: Ray, plane: FakeRayCastPlane, level: Float): Vector3 {
        val dstToPlane = plane.dstToFunc(ray.origin, ray.direction, level)
        return Vector3(ray.direction).scl(dstToPlane).add(ray.origin)
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