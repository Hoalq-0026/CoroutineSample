package com.quanghoa.apps.coroutinetodoapp.data.source

import com.quanghoa.apps.coroutinetodoapp.data.Task

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 *
 * For simplicity , this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
class TasksRepository(
    val tasksRemoteDataSource: TasksDataSource,
    val taskLocalDataSource: TasksDataSource
) : TasksDataSource {

    /**
     * This variable has public visibility so it can be accessed form tests.
     */

    var cachedTasks: LinkedHashMap<String, Task> = LinkedHashMap()

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    var cachedIsDirty = false

    /**
     * Gets task from cache, local data source(SQLite) or remote data source, whichever is available first.
     */
    override suspend fun getTasks(): Result<List<Task>> {
        // Respond immediately with cache if available and not dirty
        if (cachedTasks.isNotEmpty() && !cachedIsDirty) {
            return Result.Success(cachedTasks.values.toList())
        }
        return if (cachedIsDirty) {
            // If the cache is dirty we need to fetch new data from the network
            getTaskFromRemoteDataSource()
        } else {
            // Query the local storage if available. If not, query the network.
            val result = taskLocalDataSource.getTasks()
            when (result) {
                is Result.Success -> {
                    refreshCache(result.data)
                    Result.Success(cachedTasks.values.toList())
                }
                is Result.Error -> getTaskFromRemoteDataSource()
            }
        }
    }

    /**
     * Gets tasks from local data source unless the table is new or empty. In that case
     * it uses the network data source. This is done to simplify the sample.
     */
    override suspend fun getTask(taskId: String): Result<Task> {
        val taskInCache = getTaskWithId(taskId)

        // Respond immediately with cache if available.
        if (taskInCache != null) {
            return Result.Success(taskInCache)
        }

        // Load from server/persisted if needed

        val localResult = taskLocalDataSource.getTask(taskId)
        return when (localResult) {
            is Result.Success -> Result.Success(cache(localResult.data))
            is Result.Error -> {
                val remoteResult = tasksRemoteDataSource.getTask(taskId)
                when (remoteResult) {
                    is Result.Success -> Result.Success(cache(remoteResult.data))
                    is Result.Error -> Result.Error(RemoteDataNotFoundException())
                }
            }
        }

    }

    override suspend fun saveTask(task: Task) {
        // Do in memory cache update to keep the app UI up to date
        cache(task).let {
            tasksRemoteDataSource.saveTask(it)
            taskLocalDataSource.saveTask(it)
        }
    }

    override suspend fun completeTask(task: Task) {
        cache(task).let {
            it.isCompleted = true
            tasksRemoteDataSource.completeTask(it)
            taskLocalDataSource.completeTask(it)
        }
    }

    override suspend fun completeTask(taskId: String) {
        getTaskWithId(taskId)?.let {
            completeTask(it)
        }
    }

    override suspend fun activateTask(task: Task) {
        // Do in memory cache update to keep the app UI up to date
        cache(task).let {
            it.isCompleted = false
            tasksRemoteDataSource.activateTask(it)
            taskLocalDataSource.activateTask(it)
        }
    }

    override suspend fun activateTask(taskId: String) {
        getTaskWithId(taskId)?.let {
            activateTask(it)
        }
    }

    override suspend fun clearCompletedTask() {
        tasksRemoteDataSource.clearCompletedTask()
        taskLocalDataSource.clearCompletedTask()
        cachedTasks = cachedTasks.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, Task>
    }

    override suspend fun refreshTasks() {
        cachedIsDirty = true
    }

    override suspend fun deleteAllTasks() {
        tasksRemoteDataSource.deleteAllTasks()
        taskLocalDataSource.deleteAllTasks()
        cachedTasks.clear()
    }

    override suspend fun deleteTask(taskId: String) {
        tasksRemoteDataSource.deleteTask(taskId)
        taskLocalDataSource.deleteTask(taskId)
        cachedTasks.remove(taskId)
    }

    private suspend fun getTaskFromRemoteDataSource(): Result<List<Task>> {
        val result = tasksRemoteDataSource.getTasks()
        return when (result) {
            is Result.Success -> {
                refreshCache(result.data)
                refreshLocalDataSource(result.data)
                Result.Success(ArrayList(cachedTasks.values))
            }
            is Result.Error -> Result.Error(RemoteDataNotFoundException())
        }
    }

    private fun refreshCache(tasks: List<Task>) {
        cachedTasks.clear()
        tasks.forEach {
            cache(it)
        }
        cachedIsDirty = false
    }

    private suspend fun refreshLocalDataSource(tasks: List<Task>) {
        taskLocalDataSource.deleteAllTasks()
        for (task in tasks) {
            taskLocalDataSource.saveTask(task)
        }
    }

    private fun cache(task: Task): Task {
        val cachedTask = Task(task.title, task.description, task.id).apply {
            isCompleted = task.isCompleted
        }
        cachedTasks.put(cachedTask.id, cachedTask)
        return cachedTask
    }

    private fun getTaskWithId(id: String) = cachedTasks[id]

    companion object {
        private var INSTANCE: TasksRepository? = null

        /**
         * Return the single instance of this class, creating it if necessary.
         *
         * @param tasksRemoteDataSource the backend data source
         *
         * @param tasksLocalDataSource the device storage data source
         *
         * @return the [TasksRepository] instance
         */
        @JvmStatic
        fun getInstance(
            tasksRemoteDataSource: TasksDataSource,
            tasksLocalDataSource: TasksDataSource
        ): TasksRepository {
            return INSTANCE ?: TasksRepository(tasksRemoteDataSource, tasksLocalDataSource)
                .apply { INSTANCE = this }
        }

        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}