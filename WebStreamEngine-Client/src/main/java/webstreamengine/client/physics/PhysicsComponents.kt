package webstreamengine.client.physics

import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape
import com.badlogic.gdx.physics.bullet.collision.btSphereShape
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.EntityComponent

open class PhysicsComponent(entity: Entity, shape: btCollisionShape, offset: Vector3): EntityComponent(entity) {

    val collider = btCollisionObject()

    init {
        collider.collisionShape = shape
        collider.worldTransform = entity.generateTransformationMatrix(offset)
    }

    override fun start() { PhysicsManager.addPhysicsComponent(this) }
    override fun update() {}
    override fun stop() { PhysicsManager.removePhysicsComponent(this) }

    override fun render(batch: ModelBatch) {}
}
class SpherePhysicsComponent(entity: Entity, offset: Vector3, radius: Float): PhysicsComponent(entity, btSphereShape(radius), offset)
class BoxPhysicsComponent(entity: Entity, offset: Vector3, dimensions: Vector3): PhysicsComponent(entity, btBoxShape(dimensions), offset)