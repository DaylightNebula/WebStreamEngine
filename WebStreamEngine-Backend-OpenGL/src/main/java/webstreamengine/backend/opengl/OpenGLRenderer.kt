package webstreamengine.backend.opengl

import org.joml.Matrix4f
import org.joml.Vector4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import webstreamengine.backend.opengl.shaders.BasicTexturedShader
import webstreamengine.core.CameraInfo
import webstreamengine.core.EntityDescriptor

class OpenGLRenderer() {
    val shader = BasicTexturedShader()

    fun startRender(projection: Matrix4f) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
        shader.start()
        shader.setProjection(projection)
    }

    fun stopRender() {
        shader.stop()
    }

    fun render(descriptor: EntityDescriptor, camera: CameraInfo, mesh: OpenGLMesh) {
        // setup view matrix
        val matrix = Matrix4f()
        matrix.identity()
        matrix.scale(descriptor.scale)
        matrix.rotateX(descriptor.rotation.x - camera.rotation.x)
        matrix.rotateX(descriptor.rotation.y - camera.rotation.y)
        matrix.rotateX(descriptor.rotation.z - camera.rotation.z)
        matrix.translation(descriptor.position.x - camera.position.x, descriptor.position.y - camera.position.y, descriptor.position.z - camera.position.z)
        shader.setView(matrix)

        // render
        render(mesh)
    }

    fun render(mesh: OpenGLMesh) {
        // bind mesh that is to be rendered
        GL30.glBindVertexArray(mesh.vao)

        // enable the first vertex array
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)

        shader.setColor(Vector4f(1f, 0f, 0f, 1f))

        // draw the elements in the mesh
        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0)

        // unbind all
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL30.glBindVertexArray(0)
    }
}