package webstreamengine.client

import webstreamengine.client.application.Application
import webstreamengine.client.managers.SettingsManager
import java.io.File
import java.lang.IllegalArgumentException
import java.net.URLClassLoader

object JarInterface {

    var currentApp: Application? = null

    fun init(file: File) {
        // create a class loader for the jar
        val loader = URLClassLoader(
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

            // start current app
            SettingsManager.addAllElements(*currentApp!!.getSettings())
            currentApp!!.start()

            // log
            println("Loaded WebStreamApplication from jar file ${file.path}")
        } catch (ex: Exception) { ex.printStackTrace() }
    }

    fun getApp(): Application? {
        return currentApp
    }
}