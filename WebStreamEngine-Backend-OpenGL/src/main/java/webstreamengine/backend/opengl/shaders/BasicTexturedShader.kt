package webstreamengine.backend.opengl.shaders

import webstreamengine.core.FileUtil

class BasicTexturedShader: Shader(FileUtil.readTextFile("shaders/BasicTextured.vs"), FileUtil.readTextFile("shaders/BasicTextured.fs")) {
    override fun bindAttributes() {
        super.bindAttribute(0, "position")
        super.bindAttribute(1, "uvs")
    }

    override fun getAllUniformLocations() {}
}