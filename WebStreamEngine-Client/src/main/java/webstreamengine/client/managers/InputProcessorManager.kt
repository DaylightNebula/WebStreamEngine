package webstreamengine.client.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor

object InputProcessorManager {

    private val mult = InputMultiplexer()

    fun init() {
        Gdx.input.inputProcessor = mult
    }

    fun addProcessor(processor: InputProcessor) {
        mult.addProcessor(processor)
    }
}