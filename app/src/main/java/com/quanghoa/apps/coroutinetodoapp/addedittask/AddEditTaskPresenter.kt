package com.quanghoa.apps.coroutinetodoapp.addedittask

import com.quanghoa.apps.coroutinetodoapp.data.Task
import com.quanghoa.apps.coroutinetodoapp.data.source.Result
import com.quanghoa.apps.coroutinetodoapp.data.source.TasksRepository
import com.quanghoa.apps.coroutinetodoapp.util.launchSilent
import kotlinx.coroutines.Dispatchers
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

/**
 * Listens to user actions from the UI, retrieves the data and updates
 * the UI as required
 *
 * @param taskId ID of the task to edit or null for a new task
 *
 * @param tasksRepository a repository of data for tasks
 *
 * @param addTaskView the add/edit view
 *
 * @param isDataMissing whether data needs to be loaded or not(for config changes)
 */
class AddEditTaskPresenter(
        private val taskId: String?,
        private val tasksRepository: TasksRepository,
        private val addTaskView: AddEditTaskContract.View,
        override var isDataMissing: Boolean,
        private val uiContext: CoroutineContext = Dispatchers.Main
) : AddEditTaskContract.Presenter {

    init {
        addTaskView.presenter = this
    }

    override fun saveTask(title: String, description: String) {
        if (taskId == null) {
            createTask(title, description)
        } else {
            updateTask(title, description)
        }
    }

    override fun populateTask() = launchSilent(uiContext) {
        if (taskId == null) {
            throw RuntimeException("populateTask() was called but task is new")

        }

        val result = tasksRepository.getTask(taskId)
        if (result is Result.Success) onTaskLoaded(result.data) else onDataNotAvailable()

    }

    private fun onTaskLoaded(task: Task) {
        // The view may not be able to Handle UI updates anymore
        if (addTaskView.isActive) {
            addTaskView.setTitle(task.title)
            addTaskView.setDescription(task.description)
        }
    }

    private fun onDataNotAvailable() {
        if (addTaskView.isActive) {
            addTaskView.showEmptyTaskError()
        }
    }

    override fun start() {
        if (taskId != null && isDataMissing) {
            populateTask()
        }
    }

    private fun createTask(title: String, description: String) = launchSilent(uiContext) {
        val newTask = Task(title, description)
        if (newTask.isEmpty) {
            addTaskView.showEmptyTaskError()
        } else {
            tasksRepository.saveTask(newTask)
            addTaskView.showTaskList()
        }
    }

    private fun updateTask(title: String, description: String) = launchSilent(uiContext) {
        if (taskId == null) {
            throw RuntimeException("updateTask() was called but task is new.")
        }
        tasksRepository.saveTask(Task(title, description, taskId))
        addTaskView.showTaskList()
    }

}