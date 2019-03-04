package com.quanghoa.apps.coroutinetodoapp.data.source.remote

import com.quanghoa.apps.coroutinetodoapp.data.Task
import com.quanghoa.apps.coroutinetodoapp.data.source.LocalDataNotFoundException
import com.quanghoa.apps.coroutinetodoapp.data.source.Result
import com.quanghoa.apps.coroutinetodoapp.data.source.TasksDataSource
import kotlinx.coroutines.delay

/**
 * Implementation of the data source that adds a latency simulating network.
 */
object TasksRemoteDataSource : TasksDataSource {

    private const val SERVICE_LATENCY_IN_MILLIS = 5000L

    private var TASKS_SERVICE_DATA = LinkedHashMap<String, Task>(2)

    init {

        addTask("Build tower in Pisa", "Ground looks good, no foundation work required.")
        addTask("Finish bridge in Tocoma", "Found awesome girders at half the const!.")
    }

    private fun addTask(title: String, description: String) {
        val newTask = Task(title, description)
        TASKS_SERVICE_DATA.put(newTask.id, newTask)
    }

    override suspend fun getTasks(): Result<List<Task>> {
        val tasks = TASKS_SERVICE_DATA.values.toList()
        delay(SERVICE_LATENCY_IN_MILLIS) // Simulate network by delaying the execution.
        return Result.Success(tasks)
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        val task = TASKS_SERVICE_DATA[taskId]
        delay(SERVICE_LATENCY_IN_MILLIS)
        return if (task != null) Result.Success(task) else Result.Error(LocalDataNotFoundException())
    }

    override suspend fun saveTask(task: Task) {
        TASKS_SERVICE_DATA.put(task.id, task)
    }

    override suspend fun completeTask(task: Task) {
        val completedTask = Task(task.title, task.description, task.id).apply {
            isCompleted = true
        }
        TASKS_SERVICE_DATA.put(task.id, completedTask)
    }

    override suspend fun completeTask(taskId: String) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override suspend fun activateTask(task: Task) {
        val activeTask = Task(task.title, task.description, task.id)
        TASKS_SERVICE_DATA.put(task.id, activeTask)
    }

    override suspend fun activateTask(taskId: String) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override suspend fun clearCompletedTask() {
        TASKS_SERVICE_DATA = TASKS_SERVICE_DATA.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, Task>
    }

    override suspend fun refreshTasks() {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override suspend fun deleteAllTasks() {
        TASKS_SERVICE_DATA.clear()
    }

    override suspend fun deleteTask(taskId: String) {
        TASKS_SERVICE_DATA.remove(taskId)
    }
}