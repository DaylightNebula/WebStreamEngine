package webstreamengine.server

import java.io.File

object FBXToG3DJConverter {

    var active = false

    fun init() {
        val convFile = File(System.getProperty("user.dir"), "fbx-conv.exe")
        if (convFile.exists()) {
            println("FBX to G3DJ converter ready to go")
            active = true
        } else
            System.err.println("No fbx-conv.exe found in root folder, automatic FBX to G3DJ conversion will not be possible")
    }
}