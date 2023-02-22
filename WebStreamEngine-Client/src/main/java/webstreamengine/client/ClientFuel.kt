package webstreamengine.client

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore

lateinit var serverAddr: String
fun main(args: Array<String>) {
    println("Starting with args $args")

    // get the server address
    serverAddr = args.firstOrNull { it.startsWith("-serverPath=") }?.split("=")?.last()
        ?: throw IllegalArgumentException("Client must be given a -serverPath=<serverpath> argument")

    // if we are given arguments for a path to a keystore and its password, add that to the key store
    val keystorepath = args.firstOrNull { it.startsWith("-keyStorePath=") }?.split("=")?.last()
    val keystorepass = args.firstOrNull { it.startsWith("-keyStorePass=") }?.split("=")?.last()
    if (keystorepath != null && keystorepass != null) {
        val ksFile = File(keystorepath)
        if (ksFile.exists()) {
            FuelManager.instance.keystore = KeyStore.getInstance("JKS").apply {
                this.load(FileInputStream(ksFile), keystorepass.toCharArray())
            }
        }
    }

    // run a test
    serverAddr.httpGet().response { request, response, result ->
        println("Request $request")
        println("Response $response")
        println("Result $result")
    }

    while(true) {}
}