package webstreamengine.core

import org.joml.Matrix4f

object MatrixUtils {
    fun getOrthoProjection(dimensions: Pair<Int, Int>): Matrix4f {
        return Matrix4f().ortho2D(dimensions.first / -2f, dimensions.first / 2f, dimensions.second / -2f, dimensions.second / 2f)
    }
}