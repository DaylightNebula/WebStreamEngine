package webstreamengine.core.math

import java.lang.Math.tan

object MathUtils {
    // https://www.youtube.com/watch?v=50Y9u7K0PZo&ab_channel=ThinMatrix
    fun generatePerspectiveMatrix(dimensions: Pair<Int, Int>, fov: Double, near: Float, far: Float): Matrix4f {
        val aspect = dimensions.first.toFloat() / dimensions.second.toFloat()
        val yScale = ((1f / tan((fov / 2.0) * 0.0174532)) * aspect).toFloat()
        val xScale = yScale / aspect
        val frustum = far - near

        val matrix = Matrix4f()

        matrix.setIdentity()

        matrix.m00 = xScale
        matrix.m11 = yScale
        matrix.m22 = -((far + near) / frustum)
        matrix.m23 = -1f
        matrix.m32 = -((2 * near * far) / frustum)
        matrix.m33 = 0f

        return matrix
    }

    // https://www.youtube.com/watch?v=50Y9u7K0PZo&ab_channel=ThinMatrix
    fun generateTransformationMatrix(position: Vector3f, rotation: Vector3f, scale: Vector3f): Matrix4f {
        val matrix = Matrix4f()
        matrix.setIdentity()

        matrix.translate(position)
        matrix.rotate(rotation.x * 0.0174532f, Vector3f(1f, 0f, 0f))
        matrix.rotate(rotation.y * 0.0174532f, Vector3f(0f, 1f, 0f))
        matrix.rotate(rotation.z * 0.0174532f, Vector3f(0f, 0f, 1f))
        matrix.scale(scale)

        return matrix
    }

    // https://www.youtube.com/watch?v=50Y9u7K0PZo&ab_channel=ThinMatrix
    fun generateViewMatrix(position: Vector3f, rotation: Vector3f): Matrix4f {
        val matrix = Matrix4f()
        matrix.setIdentity()

        matrix.rotate(rotation.x * 0.0174532f, Vector3f(1f, 0f, 0f))
        matrix.rotate(rotation.y * 0.0174532f, Vector3f(0f, 1f, 0f))
        matrix.translate(Vector3f(-position.x, -position.y, -position.z))

        return matrix
    }
}