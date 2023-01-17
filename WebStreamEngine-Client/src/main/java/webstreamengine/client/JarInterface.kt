package webstreamengine.client

import webstreamengine.client.application.WebStreamApplication
import java.io.File
import java.lang.NullPointerException
import java.net.URLClassLoader

object JarInterface {

    var currentApp: WebStreamApplication? = null

    fun init(file: File) {
        // create a class loader for the jar
        val loader = URLClassLoader(
            arrayOf(file.toURI().toURL()),
            this.javaClass.classLoader
        )

        // get target class and its constructor todo dynamically load main class
        val initAppClass = Class.forName("webstreamengine.test.Tester", true, loader)
        val initAppConstructor = initAppClass.getDeclaredConstructor()

        // try to set accessible
        initAppConstructor.trySetAccessible()

        try {
            // create new instance of the app
            val initApp = initAppConstructor.newInstance()

            // make sure it is a web stream application
            if (initApp !is WebStreamApplication) throw ClassCastException("Unable to load WebStreamApplication from jar")

            // set current application
            currentApp = initApp

            // log
            println("Loaded WebStreamApplication from jar file ${file.path}")
        } catch (ex: Exception) { ex.printStackTrace() }
    }

    fun getApp(): WebStreamApplication {
        return currentApp ?: throw NullPointerException("No current WebStreamApplication jar loaded")
    }
}