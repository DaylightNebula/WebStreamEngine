package webstreamengine.client.entities

import com.badlogic.gdx.math.Vector3
import org.json.JSONObject
import javax.swing.JFrame
import kotlin.math.pow

object EntityHandler {
    // settings
    private var chunkBounds = Vector3(10f, 10f, 10f)
    private var largeEntityThreshold = 100f // threshold at which entities are considered large
    private var smallEntityRenderCutoff = 100f.pow(2f)
    private var largeEntityRenderCutoff = 10000f.pow(2f)
    private var updateCutoff = 100f.pow(2f)

    // debug window stuff
    private const val debug = false
    private val debugWindowDimensions = Pair(800, 800)
    private val visualChunkDimensions = Pair(40, 40)
    private lateinit var frame: JFrame

    // chunks
//    val globalEntities = mutableListOf<Entity>()
//    val chunks = hashMapOf<Vector3, Chunk>()
    val entities = mutableListOf<Entity>()

    init {
        if (debug) {
            frame = JFrame("Entity chunk debugger")
            frame.setSize(debugWindowDimensions.first, debugWindowDimensions.second)
            frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            frame.isResizable = false
            frame.isVisible = true
        }
    }

    fun updateSettings(json: JSONObject) {
        // update settings from json
        if (json.has("chunk_bounds")) {
            val boundsArray = json.getJSONArray("chunk_bounds")
            chunkBounds = Vector3(
                boundsArray.optFloat(0, 10f),
                boundsArray.optFloat(1, 10f),
                boundsArray.optFloat(2, 10f)
            )
        }
        largeEntityThreshold = json.optFloat("large_entity_threshold")
        largeEntityRenderCutoff = json.optFloat("large_render_threshold").pow(2f)
        smallEntityRenderCutoff = json.optFloat("small_render_threshold").pow(2f)
        updateCutoff = json.optFloat("update_cutoff").pow(2f)

        // recalculate chunks if necessary
//        if (chunks.isNotEmpty()) recalculateChunks()
    }

    private fun recalculateChunks() {
//        println("Recalculating chunks...")
//        val allEntities = mutableListOf<Entity>()
//        chunks.values.forEach { allEntities.addAll(it.largeEntities); allEntities.addAll(it.smallEntities) }
//        allEntities.forEach { addEntity(it) }
    }

    fun addEntity(entity: Entity) {
        entities.add(entity)
//        // if entity is global, add to global list and cancel
//        if (entity.global) {
//            globalEntities.add(entity)
//            return
//        }
//
//        // if entity is marked keep and no global entity with the same id exists, add to global list and cancel
//        if (entity.keep) {
//            if (!globalEntities.any { it.id == entity.id }) globalEntities.add(entity)
//            return
//        }
//
//        // add entity to chunks
//        val chunkPositions = generateChunkPositionList(entity.getPosition(), entity.box)
//        addEntityToChunkPositions(entity, chunkPositions, false)
    }
    
    fun updateEntity(entity: Entity) {
//        if (entity.global) return
//
//        // get a list of chunk positions that the entity intersects
//        val chunkPositions = generateChunkPositionList(entity.getPosition(), entity.box)
//
//        // if the list is unchanged, cancel
//        if (entity.chunks.all { chunkPositions.contains(it.chunkPosition) }) return
//
//        // get old chunks
//        val oldChunks = entity.chunks
//
//        // remove old chunks that are in the chunk positions array
//        oldChunks.removeIf { chunkPositions.contains(it.chunkPosition) }
//
//        // remove remaining old chunks that only have this as an entity
//        oldChunks.forEach {
//            if (it.largeEntities.size + it.smallEntities.size < 1) {
//                chunks.remove(it.chunkPosition)
//            } else {
//                it.largeEntities.remove(entity)
//                it.smallEntities.remove(entity)
//            }
//        }
//
//        // clear the entities chunks and add the new ones
//        entity.chunks.clear()
//        addEntityToChunkPositions(entity, chunkPositions, true)
    }

    private fun addEntityToChunkPositions(entity: Entity, chunkPositions: List<Vector3>, doContainsCheck: Boolean) {
        // check if entity meets the large entity threshold
//        val entityBounds = entity.box.bounds
//        val isLarge = entityBounds.x * entityBounds.y * entityBounds.z > largeEntityThreshold
//
//        // for each chunk position, find a chunk or create a new one
//        chunkPositions.forEach { pos ->
//            // make sure chunk exists
//            var chunk = chunks[pos]
//            if (chunk == null) {
//                chunk = Chunk(pos)
//                chunks[pos] = chunk
//            }
//
//            // add the entity to the chunk
//            entity.chunks.add(chunk)
//            val list = (if (isLarge) chunk.largeEntities else chunk.smallEntities)
//            if (doContainsCheck && list.contains(entity)) return@forEach
//            list.add(entity)
//        }
    }
    
//    fun generateChunkPositionList(offset: Vector3, box: SimpleBox): MutableList<Vector3> {
//        // min and max chunk position
//        val min = convertVectorToChunkPosition(
//            Vector3(offset)
//                .sub(Vector3(box.bounds)
//                    .scl(0.5f)
//                    .sub(box.center)
//                )
//        )
//        val max = convertVectorToChunkPosition(
//            Vector3(offset)
//                .add(Vector3(box.bounds)
//                    .scl(0.5f)
//                    .add(box.center)
//                )
//        )
//        if (debug) println("Chunk min $min max $max offset $offset bounds ${box.bounds} center ${box.center}")
//
//        // generate list of chunks between min and max
//        val vecs = mutableListOf<Vector3>()
//        for (x in min.first .. max.first) {
//            for (y in min.second .. max.second) {
//                for (z in min.third .. max.third) {
//                    vecs.add(Vector3(x.toFloat(), y.toFloat(), z.toFloat()))
//                }
//            }
//        }
//        return vecs
//    }

//    private fun convertVectorToChunkPosition(vec: Vector3): Triple<Int, Int, Int> {
//        return Triple(
//            (vec.x / chunkBounds.x).roundToInt(),
//            (vec.y / chunkBounds.y).roundToInt(),
//            (vec.z / chunkBounds.z).roundToInt(),
//        )
//    }

    fun renderEntities() {
        entities.forEach { it.clientUpdate() }
//        globalEntities.forEach { it.clientupdate() }
//
//        // get all chunks that are inside the large entity render threshold
//        chunks.values.filter { chunk -> Vector3(chunk.chunkPosition).scl(chunkBounds).dst2(camPosition) < largeEntityRenderCutoff }.forEach { chunk ->
//            // render large entities
//            chunk.largeEntities.forEach { it.clientupdate() }
//
//            // render all small entities that are within the small entity cutoff
//            chunk.smallEntities
//                .filter { it.getPosition().dst2(camPosition) < smallEntityRenderCutoff }
//                .forEach { it.clientupdate() }
//        }
//
//        // render debug window
//        if (debug) {
//            // clear frame
//            val graphics = frame.graphics
//            graphics.clearRect(0, 0, debugWindowDimensions.first, debugWindowDimensions.second)
//
//            // draw chunks
//            graphics.color = Color.RED
//            chunks.values.forEach { chunk ->
//                graphics.fillRect(
//                    (chunk.chunkPosition.x * visualChunkDimensions.first).toInt() + (debugWindowDimensions.first / 2) - (visualChunkDimensions.first / 2),
//                    (chunk.chunkPosition.z * visualChunkDimensions.second).toInt() + (debugWindowDimensions.second / 2) - (visualChunkDimensions.second / 2),
//                    visualChunkDimensions.first,
//                    visualChunkDimensions.second
//                )
//            }
//        }
    }

    fun serverUpdate() {
        entities.forEach { it.serverUpdate() }
//        globalEntities.forEach { it.update() }
//        chunks.values.filter { chunk -> Vector3(chunk.chunkPosition).scl(chunkBounds).dst2(camPosition) < updateCutoff }.forEach { chunk ->
//            (chunk.largeEntities + chunk.smallEntities).forEach { it.update() }
//        }
    }

    fun clear() {
        entities.forEach { it.dispose() }
        entities.clear()
//        globalEntities.forEach { it.dispose() }
//        globalEntities.clear()
//        chunks.values.forEach { chunk ->
//            chunk.largeEntities.forEach { it.dispose() }
//            chunk.smallEntities.forEach { it.dispose() }
//            chunk.largeEntities.clear()
//            chunk.smallEntities.clear()
//        }
//        chunks.clear()
    }
}
data class Chunk(
    val chunkPosition: Vector3,
    val requiredEntities: MutableList<Entity> = mutableListOf(),
    val largeEntities: MutableList<Entity> = mutableListOf(),
    val smallEntities: MutableList<Entity> = mutableListOf()
)