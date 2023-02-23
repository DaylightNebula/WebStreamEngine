package daylightnebula.webstreamengine.serverplugintest

import io.ktor.http.*
import webstreamengine.server.ServerPlugin

class ServerPluginTest: ServerPlugin() {
    override fun verifyParams(params: Parameters): Boolean {
        return true
    }
}