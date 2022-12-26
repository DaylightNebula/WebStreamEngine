package webstreamengine.backend.opengl

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import java.nio.FloatBuffer
import java.nio.IntBuffer

object OpenGLMeshLoader {
    val vaos = mutableListOf<Int>()
    val vbos = mutableListOf<Int>()

    fun createFloatBuffer(data: FloatArray): FloatBuffer {
        val buffer = BufferUtils.createFloatBuffer(data.size)
        buffer.put(data)
        buffer.flip()
        return buffer
    }

    fun createIntBuffer(data: IntArray): IntBuffer {
        val buffer = BufferUtils.createIntBuffer(data.size)
        buffer.put(data)
        buffer.flip()
        return buffer
    }

    fun storeData(attribute: Int, dimensions: Int, data: FloatArray) {
        // generate a vertex buffer object to store the given data
        val vbo = GL15.glGenBuffers()
        vbos.add(vbo)

        // bind to the vbo so it can be written too
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)

        // store data in the vbo
        val buffer = createFloatBuffer(data)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)

        // add some extra information to the vbo
        GL20.glVertexAttribPointer(attribute, dimensions, GL11.GL_FLOAT, false, 0, 0)

        // unbind the buffer object
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    }

    fun bindIndices(data: IntArray) {
        // generate a vertex buffer object to store the given data
        val vbo = GL15.glGenBuffers()
        vbos.add(vbo)

        // bind to the vbo so it can be written too
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo)

        // store data in the vbo
        val buffer = createIntBuffer(data)
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
    }

    fun genVAO(): Int {
        val vao = GL30.glGenVertexArrays()
        vaos.add(vao)
        GL30.glBindVertexArray(vao)
        return vao
    }

    fun createMesh(positions: FloatArray, UVs: FloatArray, indices: IntArray): OpenGLMesh {
        // generate a vertex array object (an array of buffer objects)
        val vao = genVAO()

        // store position data to the vao
        storeData(0, 3, positions)

        // store uv data to the vao
        storeData(1, 2, UVs)

        // store index data to the vao
        bindIndices(indices)

        // unbind vao
        GL30.glBindVertexArray(0)

        // return new mesh
        return OpenGLMesh(vao, indices.size)
    }
}
class OpenGLMesh(val vao: Int, val vertexCount: Int)