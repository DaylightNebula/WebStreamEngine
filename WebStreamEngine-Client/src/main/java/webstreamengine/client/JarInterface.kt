package webstreamengine.client

import org.json.JSONObject
import webstreamengine.client.application.Application
import webstreamengine.client.entities.EntityChunks
import webstreamengine.client.inputs.InputManager
import webstreamengine.client.managers.SettingsManager
import java.io.File
import java.lang.IllegalArgumentException
import java.net.URLClassLoader

object JarInterface {

    lateinit var loader: URLClassLoader
    var currentApp: Application? = null

    fun init(file: File) {
        // create a class loader for the jar
        loader = URLClassLoader(
            arrayOf(file.toURI().toURL()),
            this.javaClass.classLoader
        )

        val setup = loader.getResource("setup.config")?.readText()?.split("\n") ?: throw IllegalArgumentException("Jar file does not have a setup.config")
        val mainClass = setup.firstOrNull { it.startsWith("MAIN_CLASS") }?.split("=")?.last() ?: throw IllegalArgumentException("setup.config does not specify a main class")

        // get target class and its constructor
        val initAppClass = Class.forName(mainClass, true, loader)
        val initAppConstructor = initAppClass.getDeclaredConstructor()

        // try to set accessible
        initAppConstructor.trySetAccessible()

        try {
            // create new instance of the app
            val initApp = initAppConstructor.newInstance()

            // make sure it is a web stream application
            if (initApp !is Application) throw ClassCastException("Unable to load WebStreamApplication from jar")

            // set current application
            currentApp = initApp

            // load built-in configs
            SettingsManager.addAllElements(*currentApp!!.getSettings())
            InputManager.loadInputDefaults(loader.getResource("input.json")?.readText())
            loader.getResource("chunk_settings.json")?.let { EntityChunks.updateSettings(JSONObject(it.readText())) }

            // start current app
            currentApp!!.start()

            // log
            println("Loaded WebStreamApplication from jar file ${file.path}")
        } catch (ex: Exception) { ex.printStackTrace() }
    }

    fun getTextResource(path: String): String? {
        return loader.getResource(path)?.readText()
    }

    fun getApp(): Application? {
        return currentApp
    }
}