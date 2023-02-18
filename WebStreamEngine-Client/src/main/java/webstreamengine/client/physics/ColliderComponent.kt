package webstreamengine.client.physics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.softbody.btSoftColliders.CollideVF_SS
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.EntityChunks
import webstreamengine.client.entities.EntityComponent
import kotlin.math.abs

class ColliderComponent(
    entity: Entity,
    val box: SimpleBox,
    val hasGravity: Boolean,
    val rayCastOnly: Boolean = false
): EntityComponent(entity) {

    val velocity = Vector3(0f, 0f, 0f)
    private var onGroundScore = 0

    override fun update() {
        // if we are not moving and no gravity, we don't need to do anything
        if (!hasGravity && velocity == Vector3.Zero) return

        // update velocities and position
        if (hasGravity)
            velocity.y += PhysicsController.gravity * Gdx.graphics.deltaTime

        // get a copy of box, expanded by velocities magnitude
        val velBox = SimpleBox(box.center, Vector3(box.bounds).scl(velocity.x * Gdx.graphics.deltaTime + 1f, velocity.y * Gdx.graphics.deltaTime + 1f, velocity.z * Gdx.graphics.deltaTime + 1f))

        val colliders = PhysicsController.getCollidersInBox(entity.getPosition(), velBox)

        // if we have colliders, limit velocity accordingly
        if (onGroundScore > 0) onGroundScore--
        if (colliders.isNotEmpty()) {
            // loop through colliders to limit velocity
            colliders.forEach { other ->
                // some stuff to aid in intersect tests
                val so2 = Vector3(entity.getPosition()).add(velBox.center)
                val oo2 = Vector3(other.entity.getPosition()).add(other.box.center)

                // do the same if xz intersect, except change y component
                if (abs(entity.getPosition().x + velBox.center.x - oo2.x) < abs((box.bounds.x + other.box.bounds.x) / 2) && abs(entity.getPosition().z + velBox.center.z - oo2.z) < abs((box.bounds.z + other.box.bounds.z) / 2)) {
                    velocity.y = 0f
                    val yDist = oo2.y - so2.y
                    val moveAmount = if (yDist < 0)
                        yDist + ((box.bounds.y / 2f + box.center.y) + (other.box.bounds.y / 2f + other.box.center.y)) + 0.001f
                    else
                        yDist + ((box.bounds.y / 2f - box.center.y) - (other.box.bounds.y / 2f + other.box.center.y)) - 0.001f
                    entity.move(Vector3(0f, moveAmount, 0f))
                    onGroundScore = 3
                }

                // if xy intersect, remove the z velocity component and move the entity to the z side of other
                if (abs(entity.getPosition().x + velBox.center.x - oo2.x) < abs((box.bounds.x + other.box.bounds.x) / 2) && abs(entity.getPosition().y + velBox.center.y - oo2.y) < abs((box.bounds.y + other.box.bounds.y) / 2)) {
                    velocity.z = 0f
                    val zDist = oo2.z - so2.z
                    val moveAmount = if (zDist < 0)
                        zDist + ((box.bounds.z / 2 + box.center.z) + (other.box.bounds.z / 2 - other.box.center.z)) + 0.001f
                    else
                        zDist + ((box.bounds.z / 2 - box.center.z) - (other.box.bounds.z / 2 + other.box.center.z)) - 0.001f
                    entity.move(Vector3(0f, 0f, moveAmount))
                }

                // and finally do the same for the yz axis, except change the x component
                if (abs(entity.getPosition().y + velBox.center.y - oo2.y) < abs((box.bounds.y + other.box.bounds.y) / 2) && abs(entity.getPosition().z + velBox.center.z - oo2.z) < abs((box.bounds.z + other.box.bounds.z) / 2)) {
                    velocity.x = 0f
                    val xDist = oo2.x - so2.x
                    val moveAmount = if (xDist < 0)
                        xDist + ((box.bounds.x / 2 + box.center.x) + (other.box.bounds.x / 2 - other.box.center.x)) + 0.001f
                    else
                        xDist + ((box.bounds.x / 2 - box.center.x) - (other.box.bounds.x / 2 + other.box.center.x)) - 0.001f
                    entity.move(Vector3(moveAmount, 0f, 0f))
                }
            }
        }

        if (onGroundScore > 0)
            velocity.sub(Vector3(velocity).scl(PhysicsController.onGroundDragMult * Gdx.graphics.deltaTime))

        // finally apply what's left of the velocity
        entity.move(Vector3(velocity).scl(Gdx.graphics.deltaTime))
    }


    fun isMoveValid(move: Vector3): Boolean {
        val scl = Vector3(move.x, (move.y), (move.z))
        val velBox = SimpleBox(Vector3(box.center).add(Vector3(scl).scl(0.5f)), Vector3(box.bounds).scl(abs(scl.x) + 1f, abs(scl.y) + 1f, abs(scl.z) + 1f))
        val collisions = PhysicsController.getCollidersInBox(entity.getPosition(), velBox)
        return if (onGroundScore < 3) collisions.size < 2 else collisions.isEmpty()
    }

    fun isOnGround(): Boolean { return onGroundScore > 0 }

    override fun start() {}
    override fun stop() {}
    override fun render(batch: ModelBatch) {}
}