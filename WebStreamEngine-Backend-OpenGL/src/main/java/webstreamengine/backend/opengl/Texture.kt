package webstreamengine.backend.opengl

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import java.io.File

object Texture {
    val idMap = hashMapOf<String, Int>()

    fun loadTexture(path: String): Int {
        // if texture has already been created, return that texture
        if (idMap.containsKey(path)) return idMap[path]!!

        // get a memory stack to use
        MemoryStack.stackPush().use { stack ->
            // setup stack pointers
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)
            val pChannels = stack.mallocInt(1)

            // get the textures file
            val file = File(System.getProperty("user.dir"), path)

            // load the texture
            val buffer = STBImage.stbi_load(file.absolutePath, pWidth, pHeight, pChannels, 4)
                ?: throw Exception("Could not load file ${file.absolutePath} with reason ${STBImage.stbi_failure_reason()}")

            val width = pWidth.get()
            val height = pHeight.get()

            // generate texture
            val textureID = GL11.glGenTextures()
            idMap.put(path, textureID)

            // bind texture
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID)
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)

            // load texture
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer)

            // generate mip maps
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)

            // free up STB
            STBImage.stbi_image_free(buffer)

            // if we made it this far successfully, return the texture id
            return textureID
        }
    }
}