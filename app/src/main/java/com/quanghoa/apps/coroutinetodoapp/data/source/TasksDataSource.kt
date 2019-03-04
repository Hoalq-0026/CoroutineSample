package com.quanghoa.apps.coroutinetodoapp.data.source

import com.quanghoa.apps.coroutinetodoapp.data.Task

/**
 * Main entry point for accessing tasks data.
 *
 * For simplicity, only getTasks() and getTask() return Result object. Consider adding Result to other
 * methods to inform the user of network/database errors on successful operations.
 *
 * For example, when new task is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 *
 */
interface TasksDataSource {

    suspend fun getTasks(): Result<List<Task>>

    suspend fun getTask(taskId: String): Result<Task>

    suspend fun saveTask(task: Task)

    suspend fun completeTask(task: Task)

    suspend fun completeTask(taskId: String)

    suspend fun activateTask(task: Task)

    suspend fun activateTask(taskId: String)

    suspend fun clearCompletedTask()

    suspend fun refreshTasks()

    suspend fun deleteAllTasks()

    suspend fun deleteTask(taskId: String)
}