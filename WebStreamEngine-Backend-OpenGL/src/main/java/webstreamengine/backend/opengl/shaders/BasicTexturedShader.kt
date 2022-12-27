package webstreamengine.backend.opengl.shaders

import webstreamengine.core.FileUtil
import org.joml.Matrix4f
import kotlin.properties.Delegates

class BasicTexturedShader: Shader(FileUtil.readTextFile("shaders/BasicTextured.vs"), FileUtil.readTextFile("shaders/BasicTextured.fs")) {

    var projectionMatrixLocation = 0
    var viewMatrixLocation = 0

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
        super.bindAttribute(1, "uvs")
    }

    override fun getAllUniformLocations() {
        projectionMatrixLocation = getUniformLocation("projectionMatrix")
        viewMatrixLocation = getUniformLocation("viewMatrix")
    }

    fun setProjectionMatrix(matrix: Matrix4f) {
        super.loadMatrix(projectionMatrixLocation, matrix)
        println("New Projection Matrix: \n$matrix===================")
    }

    fun setViewMatrix(matrix: Matrix4f) {
        super.loadMatrix(viewMatrixLocation, matrix)
    }
}