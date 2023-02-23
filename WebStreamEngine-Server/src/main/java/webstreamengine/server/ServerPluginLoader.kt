package webstreamengine.server

import java.io.File
import java.net.URLClassLoader

object ServerPluginLoader {
    val plugins = mutableListOf<ServerPlugin>()
    private val serverPluginsFolder = File("server_plugins")

    fun init() {
        // make sure server plugins folder exists
        if (!serverPluginsFolder.exists())
            serverPluginsFolder.mkdirs()

        // loop through all child files and if that file is a jar file, load it
        serverPluginsFolder.listFiles()?.forEach {
            if (it.extension == "jar")
                loadJar(it)
        }
    }

    private fun loadJar(file: File) {
        // get loader
        val loader = URLClassLoader(arrayOf(file.toURI().toURL()))

        // get main class
        val config = loader.getResource("config.properties")?.readText()?.split("\n")
            ?: throw IllegalArgumentException("config.properties required in Server Plugins")
        val mainClass = config.firstOrNull { it.startsWith("MAIN_CLASS=") }?.split("=")?.last()
            ?: throw IllegalArgumentException("config.properties needs a MAIN_CLASS")

        // get plugin class and its constructor
        val pluginClass = Class.forName(mainClass, true, loader)
        val pluginConstructor = pluginClass.getDeclaredConstructor()
        pluginConstructor.trySetAccessible()

        // create plugin
        val plugin = pluginConstructor.newInstance() as ServerPlugin

        // save
        plugins.add(plugin)
        println("Loaded plugin ${file.absolutePath}")
    }
}