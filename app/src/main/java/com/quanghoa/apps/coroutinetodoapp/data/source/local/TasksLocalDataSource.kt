package com.quanghoa.apps.coroutinetodoapp.data.source.local

import android.support.annotation.VisibleForTesting
import com.quanghoa.apps.coroutinetodoapp.data.Task
import com.quanghoa.apps.coroutinetodoapp.data.source.LocalDataNotFoundException
import com.quanghoa.apps.coroutinetodoapp.data.source.Result
import com.quanghoa.apps.coroutinetodoapp.data.source.TasksDataSource
import com.quanghoa.apps.coroutinetodoapp.util.AppExecutors
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of a data source as a db.
 */
class TasksLocalDataSource private constructor(
    val appExecutors: AppExecutors,
    val tasksDao: TasksDao
) : TasksDataSource {
    override suspend fun getTasks(): Result<List<Task>> = withContext(appExecutors.ioContext) {
        val tasks = tasksDao.getTasks()
        if (tasks.isNotEmpty()) {
            Result.Success(tasksDao.getTasks())
        } else {
            Result.Error(LocalDataNotFoundException())
        }
    }

    override suspend fun getTask(taskId: String): Result<Task> = withContext(appExecutors.ioContext) {
        val task = tasksDao.getTaskById(taskId)
        if (task != null) Result.Success(task) else Result.Error(LocalDataNotFoundException())
    }

    override suspend fun saveTask(task: Task) = withContext(appExecutors.ioContext) {
        tasksDao.insertTask(task)
    }

    override suspend fun completeTask(task: Task) = withContext(appExecutors.ioContext) {
        tasksDao.updateCompleted(taskId = task.id, completed = true)
    }

    override suspend fun completeTask(taskId: String) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override suspend fun activateTask(task: Task) = withContext(appExecutors.ioContext) {
        tasksDao.updateCompleted(task.id, false)
    }

    override suspend fun activateTask(taskId: String) = withContext(appExecutors.ioContext) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override suspend fun clearCompletedTask() {
        withContext(appExecutors.ioContext) {
            tasksDao.deleteCompletedTasks()
        }
    }

    override suspend fun refreshTasks() {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override suspend fun deleteAllTasks() = withContext(appExecutors.ioContext) {
        tasksDao.deleteTasks()
    }

    override suspend fun deleteTask(taskId: String) {
        withContext(appExecutors.ioContext) {
            tasksDao.deleteTaskById(taskId)
        }
    }

    companion object {
        private var INSTANCE: TasksLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, tasksDao: TasksDao): TasksLocalDataSource {
            if (INSTANCE == null) {
                synchronized(TasksLocalDataSource::javaClass) {
                    INSTANCE = TasksLocalDataSource(appExecutors, tasksDao)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }

}