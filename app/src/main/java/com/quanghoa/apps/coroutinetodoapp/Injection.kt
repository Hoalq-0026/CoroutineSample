package com.quanghoa.apps.coroutinetodoapp

import android.content.Context
import com.quanghoa.apps.coroutinetodoapp.data.source.TasksRepository
import com.quanghoa.apps.coroutinetodoapp.data.source.local.TasksLocalDataSource
import com.quanghoa.apps.coroutinetodoapp.data.source.local.TodoDatabase
import com.quanghoa.apps.coroutinetodoapp.data.source.remote.TasksRemoteDataSource
import com.quanghoa.apps.coroutinetodoapp.util.AppExecutors

/**
 * Enables injection of mock implementations for
 * [TasksDataSource] at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
object Injection {

    fun provideTasksRepository(context: Context): TasksRepository {

        val database = TodoDatabase.getIntance(context)
        return TasksRepository.getInstance(
            TasksRemoteDataSource,
            TasksLocalDataSource.getInstance(AppExecutors(), database.taskDao())
        )
    }
}