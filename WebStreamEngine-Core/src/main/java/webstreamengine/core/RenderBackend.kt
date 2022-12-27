package webstreamengine.core

import org.joml.Vector3f

abstract class RenderBackend(val info: RenderBackendInfo): Thread() {
    // general management stuffs (no start since run is start in a thread)
    abstract fun close()
    abstract fun shouldClose(): Boolean
    abstract fun isLoadingComplete(): Boolean

    // mesh stuffs
    abstract fun loadMesh(info: MeshInfo): Int

    // texture stuffs
    abstract fun loadLocalTexture(path: String): Int

    // entity stuffs
    abstract fun addOrUpdateEntityDescriptor(id: Int, descriptor: EntityDescriptor)
    abstract fun clearCurrentEntities()

    // camera stuffs
    abstract fun updateCameraInfo(info: CameraInfo)
}
data class RenderBackendInfo(
    val winName: String,
    val winDimensions: Pair<Int, Int>
)
data class EntityDescriptor(
    val position: Vector3f,
    val rotation: Vector3f,
    val scale: Vector3f,
    val mesh: Int? = null,
    val texture: Int? = null
)
data class CameraInfo(
    val position: Vector3f,
    val rotation: Vector3f,
    val fov: Float,
    val near: Float,
    val far: Float
)
data class MeshInfo(
    val vertices: FloatArray,
    val uvs: FloatArray,
    val indices: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MeshInfo

        if (!vertices.contentEquals(other.vertices)) return false
        if (!uvs.contentEquals(other.uvs)) return false
        if (!indices.contentEquals(other.indices)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vertices.contentHashCode()
        result = 31 * result + uvs.contentHashCode()
        result = 31 * result + indices.contentHashCode()
        return result
    }
}