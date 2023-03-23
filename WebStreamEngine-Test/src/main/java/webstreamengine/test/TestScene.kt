package webstreamengine.test

import org.json.JSONObject
import webstreamengine.client.entities.Entity
import webstreamengine.client.networking.Connection
import webstreamengine.client.scenes.Scene
import java.util.*

class TestScene: Scene("test_scene") {
    override fun generalStart() {
    }

    override fun serverUpdate() {}

    override fun clientUpdate() {}

    override fun generalStop() {
    }

    override fun netJoin(conn: Connection) {
        Entity.createFromPath(UUID.randomUUID(), false, "player") {
            it.assignedTo = conn.id
        }
    }

    override fun netDisconnect(conn: Connection) {

    }

    override fun netCommunicate(json: JSONObject) {}
}