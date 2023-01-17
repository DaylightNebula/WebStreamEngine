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

        // load options from loadconfig.txt
        println("Loading config options")
        val configtext = File(System.getProperty("user.dir"), "loadconfig.txt")
        val configlines = configtext.readLines()
        val configoptions = hashMapOf<String, String>()
        configlines.forEach { line ->
            val tokens = line.split("=", limit = 2)
            configoptions[tokens[0]] = tokens[1]
            println("Option ${tokens[0]} set to ${tokens[1]}")
        }
        println("Finished loading config options")

        // get target class and its constructor
        val initAppClass = Class.forName(configoptions["MAIN_CLASS"], true, loader)
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

            // start current app
            currentApp!!.start()

            // log
            println("Loaded WebStreamApplication from jar file ${file.path}")
        } catch (ex: Exception) { ex.printStackTrace() }
    }

    fun getApp(): WebStreamApplication? {
        return currentApp
    }
}