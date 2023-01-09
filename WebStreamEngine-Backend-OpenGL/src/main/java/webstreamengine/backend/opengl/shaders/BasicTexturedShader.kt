package webstreamengine.backend.opengl.shaders

import webstreamengine.core.FileUtil
import org.joml.Matrix4f
import org.joml.Vector4f
import kotlin.properties.Delegates

class BasicTexturedShader: Shader(FileUtil.readTextFile("shaders/BasicTextured.vs"), FileUtil.readTextFile("shaders/BasicTextured.fs")) {

    var locationProjection = 0
    var locationView = 0
    var locationColor = 0

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
        super.bindAttribute(1, "uvs")
        super.bindAttribute(2, "color")
    }

    override fun getAllUniformLocations() {
        locationProjection = getUniformLocation("projection")
        locationView = getUniformLocation("view")
        locationColor = getUniformLocation("color")
    }

    fun setProjection(matrix: Matrix4f) {
        super.loadMatrix(locationProjection, matrix)
    }

    fun setView(matrix: Matrix4f) {
        super.loadMatrix(locationView, matrix)
    }

    fun setColor(color: Vector4f) {
        super.loadVector4(locationColor, color)
    }
}