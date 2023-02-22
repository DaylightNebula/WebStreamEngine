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

    fun convertFile(file: File) {
        // get target files and their local paths
        val targetLocalPath = FileHandler.getLocalPath(file)
        val finalFile = File(file.absolutePath.replace("fbx", "g3dj"))
        val finalFileLocalPath = FileHandler.getLocalPath(finalFile)

        // if a final file already exists, cancel
        if (finalFile.exists()) {
            file.delete()
            return
        }

        println("Converting FBX to G3DJ ${file.absolutePath}")
        val builder = ProcessBuilder("cmd.exe", "/c", ".\\fbx-conv.exe -o g3dj -f -v $targetLocalPath $finalFileLocalPath")
        builder.redirectErrorStream(true)
        val process = builder.start()

        // wait for the conversion process to end
        process.waitFor()

        file.delete()
    }
}