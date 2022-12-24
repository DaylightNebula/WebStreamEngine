package webstreamengine.backend.opengl

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20

abstract class Shader(vertexSource: String, fragSource: String) {
    val matrix = BufferUtils.createFloatBuffer(16)

    val programID: Int
    val vertexID: Int
    val fragID: Int

    init {
        // load vertex and fragment shaders
        vertexID = loadShader(vertexSource, GL20.GL_VERTEX_SHADER)
        fragID = loadShader(fragSource, GL20.GL_FRAGMENT_SHADER)

        // create shader program
        programID = GL20.glCreateProgram()

        // attach shaders to the shader program
        GL20.glAttachShader(programID, vertexID)
        GL20.glAttachShader(programID, fragID)

        // bind attributes to the shader
        bindAttributes()

        // link and validate shader program
        GL20.glLinkProgram(programID)
        GL20.glValidateProgram(programID)

        // setup uniform locations
        getAllUniformLocations()
    }

    fun start() {
        GL20.glUseProgram(programID)
    }

    fun stop() {
        GL20.glUseProgram(0)
    }

    protected fun getUniformLocation(uniformName: String): Int {
        return GL20.glGetUniformLocation(programID, uniformName)
    }

    protected fun bindAttribute(attribute: Int, variableName: String) {
        GL20.glBindAttribLocation(programID, attribute, variableName)
    }

    protected open fun loadFloat(location: Int, value: Float) {
        GL20.glUniform1f(location, value)
    }

    protected open fun loadVector(location: Int, vector: Vector3f) {
        GL20.glUniform3f(location, vector.x, vector.y, vector.z)
    }

    protected open fun loadBoolean(location: Int, value: Boolean) {
        var tovec = 0f
        if (value) {
            tovec = 1f
        }
        GL20.glUniform1f(location, tovec)
    }

    protected open fun loadMatrix(location: Int, value: Matrix4f) {
        value.store(matrix)
        matrix.flip()
        GL20.glUniformMatrix4(location, false, matrix)
    }

    protected abstract fun bindAttributes()
    protected abstract fun getAllUniformLocations()

    companion object {
        fun loadShader(text: String, type: Int): Int {
            // create a shader of the given type
            val id = GL20.glCreateShader(type)

            // load and compile the shader
            GL20.glShaderSource(id, text)
            GL20.glCompileShader(id)

            // if the shader could not compile, log the error and crash
            if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                println(GL20.glGetShaderInfoLog(id, 512))
                System.err.println("Couldn't compile the shader")
                System.exit(-1);
            }

            // return the shaders id
            return id
        }
    }
}