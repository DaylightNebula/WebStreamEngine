package webstreamengine.client.managers

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.Ray
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.components.PhysicsComponent

object PhysicsManager {

    // physics world stuff
    lateinit var collisionConfig: btDefaultCollisionConfiguration
    lateinit var dispatcher: btCollisionDispatcher
    lateinit var broadphase: btDbvtBroadphase
    lateinit var world: btCollisionWorld

    // component tracking stuff
    val components = mutableListOf<PhysicsComponent>()

    fun init() {
        // setup bullet physics
        Bullet.init()

        // setup physics world
        collisionConfig = btDefaultCollisionConfiguration()
        dispatcher = btCollisionDispatcher(collisionConfig)
        broadphase = btDbvtBroadphase()
        world = btCollisionWorld(dispatcher, broadphase, collisionConfig)
    }

    fun addPhysicsComponent(component: PhysicsComponent) {
        println("Added physics component $component")
        world.addCollisionObject(component.collider)
        components.add(component)
    }

    fun removePhysicsComponent(component: PhysicsComponent) {
        world.removeCollisionObject(component.collider)
        components.remove(component)
    }

    data class RayCastResult(val component: PhysicsComponent, val result: ClosestRayResultCallback)
    fun rayCast(ray: Ray, maxDistance: Float = 100f): RayCastResult? {
        // setup from and to vectors
        val rayFrom = Vector3(ray.origin)
        val rayTo = Vector3(ray.direction).scl(maxDistance).add(rayFrom)

        // create collision callback
        val collisionCallback = ClosestRayResultCallback(rayFrom, rayTo)
        collisionCallback.closestHitFraction = .1f

        // run ray cast
        world.rayTest(rayFrom, rayTo, collisionCallback)

        // if we have hit something, return results
        if (collisionCallback.hasHit()) {
            return RayCastResult(
                components.first { it.collider == collisionCallback.collisionObject },
                collisionCallback
            )
        }

        // if nothing was found, return nothing
        return null
    }
}