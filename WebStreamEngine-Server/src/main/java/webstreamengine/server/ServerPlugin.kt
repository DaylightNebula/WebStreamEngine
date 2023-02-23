package webstreamengine.server

import io.ktor.http.*

abstract class ServerPlugin {
    abstract fun verifyParams(params: Parameters): Boolean
}