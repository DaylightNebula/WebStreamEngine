package webstreamengine.server

import webstreamengine.core.ByteUtils
import webstreamengine.core.MeshInfo
import java.io.File

object ServerMeshHandler {
    private val meshes = hashMapOf<String, ByteArray>() // format: Mesh ID, mesh information

    fun init() {
        // go through all files in the assets directory and load any files
        val file = File(System.getProperty("user.dir"), "assets")
        if (!file.exists() || !file.isDirectory)
            file.mkdirs()
        loadMeshFilesRecursively(file)
    }

    fun loadMeshFilesRecursively(root: File) {
        root.listFiles()?.forEach { file->
            if (file.isDirectory)
                loadMeshFilesRecursively(file)
            else if (file.extension == "obj")
                loadOBJMesh(file)
        }
    }

    fun loadOBJMesh(file: File) {
        // read lines of file
        val lines = file.readLines()

        val meshID = file.nameWithoutExtension

        // setup mesh info lists
        val vertices = mutableListOf<Float>()
        val normals = mutableListOf<Float>()
        val uvs = mutableListOf<Float>()
        val indices = mutableListOf<Int>()

        // go through each line and load each line into lists accordingly
        lines.forEach { line ->
            val tokens = line.split(" ")
            when(tokens[0]) {
                "v" -> {
                    vertices.add(tokens[1].toFloat())
                    vertices.add(-tokens[2].toFloat())
                    vertices.add(tokens[3].toFloat())
                }
                "vt" -> {
                    uvs.add(tokens[1].toFloat())
                    uvs.add(tokens[2].toFloat())
                }
                "vn" -> {
                    normals.add(tokens[1].toFloat())
                    normals.add(tokens[2].toFloat())
                    normals.add(tokens[3].toFloat())
                }
                "f" -> { // todo deal with individual texture and normal coords
                    indices.add(tokens[1].split("/").first().toInt() - 1)
                    indices.add(tokens[2].split("/").first().toInt() - 1)
                    indices.add(tokens[3].split("/").first().toInt() - 1)
                }
                else -> {}
            }
        }

        println("Num vertices ${vertices.size}, first ${vertices.first()}")

        val meshinfo = MeshInfo(vertices.toFloatArray(), uvs.toFloatArray(), normals.toFloatArray(), indices.toIntArray())
        meshes[meshID] = byteArrayOf(
            *ByteUtils.convertStringToByteArray(meshID),
            *meshinfo.convertToByteArray()
        )

        println("Loaded mesh ${meshID} with a hash of ${meshinfo.hashCode()}")
    }

    fun requestMesh(meshID: String): ByteArray? {
        return meshes[meshID]
    }
}