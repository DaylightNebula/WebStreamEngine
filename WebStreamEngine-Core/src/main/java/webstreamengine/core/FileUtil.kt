package webstreamengine.core

import java.io.File

object FileUtil {
    fun readTextFile(path: String): String {
        return File(System.getProperty("user.dir"), path).readText()
    }
}