package webstreamengine.client

import webstreamengine.core.*

object ClientMeshHandler {
    private val entitiesWaitingForMesh = hashMapOf<String, MutableList<EntityDescriptor>>() // format: Mesh ID, list of entities waiting for mesh
    private val requestedMesh = mutableListOf<String>() // list of mesh ids
    private val meshes = hashMapOf<String, Int>() // Format: Mesh ID, Mesh Backend ID

    fun applyMeshToEntity(entity: EntityDescriptor, conn: Connection, meshID: String) {
        // if we already have a mesh of the given id, pass the id to the entity
        if (meshes.contains(meshID)) {
            entity.mesh = meshes[meshID]
            return
        }

        // otherwise, send a request to the server for the mesh
        conn.sendPacket(
            PacketUtils.generatePacket(
                PacketType.REQUEST_MESH,
                ByteUtils.convertStringToByteArray(meshID)
            )
        )
        println("Requested $meshID")

        // get the list of entities waiting for the given mesh, and if it is blank make a new one, then add this entity to that list
        var list = entitiesWaitingForMesh[meshID]
        if (list == null) {
            list = mutableListOf()
            entitiesWaitingForMesh[meshID] = list
        }
        list.add(entity)
    }

    /**
     * meshID: String
     * meshHashCode: Int
     * vertices: FloatArray
     * uvs: FloatArray
     * normals: FloatArray
     * indices: IntArray
     */
    fun handleMeshDelivery(backend: RenderBackend, reader: ByteReader) {
        // load all mesh info from the incoming packet
        val meshID = reader.nextString()
        println("Starting to load mesh $meshID")
        val meshHash = reader.nextInt()
        println("It has a hash of $meshHash")

//        val numVertices = reader.nextInt()
//        println("Num vertices $numVertices, first ${reader.nextFloat()}")

        val vertices = reader.nextFloatArray()
        val uvs = reader.nextFloatArray()
        val normals = reader.nextFloatArray()
        val indices = reader.nextIntArray()

        // build a mesh info object from the given data
        val meshInfo = MeshInfo(vertices, uvs, normals, indices)

        // send the mesh info to the backend and get its corresponding mesh backend id
        val meshBackendID = backend.loadMesh(meshInfo)

        // save to meshes
        meshes[meshID] = meshBackendID

        // remove from requested mesh list
        requestedMesh.remove(meshID)

        // update all waiting entities
        entitiesWaitingForMesh[meshID]?.forEach { it.mesh = meshBackendID }
        entitiesWaitingForMesh.remove(meshID)

        println("Received and loaded mesh $meshID")
    }
}