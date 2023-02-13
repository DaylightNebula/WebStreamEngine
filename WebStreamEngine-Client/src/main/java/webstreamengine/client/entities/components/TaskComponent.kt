package webstreamengine.client.entities.components

import com.badlogic.gdx.graphics.g3d.ModelBatch
import webstreamengine.client.entities.Entity
import webstreamengine.client.entities.EntityComponent

class TaskComponent(entity: Entity, private val tasks: MutableList<Task>): EntityComponent(entity) {

    private var currentTask: Task? = null

    fun addTask(task: Task) {
        tasks.add(task)
    }

    fun removeTask(task: Task) {
        if (currentTask == task) {
            task.stop()
            currentTask = null
        }
        tasks.remove(task)
    }

    override fun update() {
        // remove any tasks that need to be removed
        tasks.removeIf { it.shouldTaskBeRemoved() }

        // check if there is a possibility of the current task changing
        if (currentTask == null || currentTask!!.isComplete() || currentTask!!.canBeInterrupted()) {
            // try to get a task with the highest priority that is over 0
            var newTask = tasks.maxByOrNull { it.getPriority() }
            if ((newTask?.getPriority() ?: 0f) <= 0f) newTask = null

            // if the task changed, stop the current task, swap to the new one, and then start the new one
            if (newTask != currentTask) {
                currentTask?.stop()
                currentTask = newTask
                currentTask?.start()
            }
        }
    }

    override fun stop() {}
    override fun start() {}
    override fun render(batch: ModelBatch) {}
}
abstract class Task(val entity: Entity) {
    abstract fun start()
    abstract fun update()
    abstract fun stop()

    abstract fun getPriority(): Float
    abstract fun isComplete(): Boolean
    abstract fun canBeInterrupted(): Boolean
    abstract fun shouldTaskBeRemoved(): Boolean
}