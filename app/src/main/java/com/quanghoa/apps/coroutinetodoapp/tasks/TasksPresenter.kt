package com.quanghoa.apps.coroutinetodoapp.tasks

import com.quanghoa.apps.coroutinetodoapp.data.Task
import com.quanghoa.apps.coroutinetodoapp.data.source.Result
import com.quanghoa.apps.coroutinetodoapp.data.source.TasksRepository
import com.quanghoa.apps.coroutinetodoapp.util.launchSilent
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * Listener to user action form the UI[TasksFragment], retrieves the data and updates the
 * UI as required
 */
class TasksPresenter(private val tasksRepository: TasksRepository,
                     private val tasksView: TasksContract.View,
                     private val uiContext: CoroutineContext = Dispatchers.Main)
    : TasksContract.Presenter {

    override var currentFiltering = TasksFilterType.ALL_TASKS

    private var firstLoad = true

    init {
        tasksView.presenter = this
    }


    override fun result(requestCode: Int, resultCode: Int) {
        // If a task was successfully added, show snackbar
        // TODO later
    }

    override fun loadTasks(forceUpdate: Boolean) {
        // Simplification for sample: a network reload will be forced on first load.
        loadTasks(forceUpdate || firstLoad, true)
        firstLoad = false
    }

    override fun addNewTask() {
        tasksView.showAddTask()
    }

    override fun openTaskDetails(requestedTask: Task) {
        tasksView.showTaskDetailsUi(requestedTask.id)
    }

    override fun completeTask(completedTask: Task) = launchSilent(uiContext) {
        tasksRepository.completeTask(completedTask)
        tasksView.showTaskMarkedComplete()
        loadTasks(false, false)
    }

    override fun activateTask(activeTask: Task) = launchSilent(uiContext) {
        tasksRepository.activateTask(activeTask)
        tasksView.showTaskMarkedActive()
        loadTasks(false, false)
    }

    override fun clearCompletedTask() = launchSilent {
        tasksRepository.clearCompletedTask()
        tasksView.showCompletedTasksCleared()
        loadTasks(false, false)
    }

    override fun start() {
        loadTasks(false)
    }

    /**
     * @param forceUpdate Pass in true to refresh the data in the [TasksDataSource]
     *
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private fun loadTasks(forceUpdate: Boolean, showLoadingUI: Boolean) = launchSilent(uiContext) {
        if (showLoadingUI) {
            tasksView.setLoadingIndicator(true)
        }

        if (forceUpdate) {
            tasksRepository.refreshTasks()
        }

        val result = tasksRepository.getTasks()
        if (result is Result.Success) {
            val tasksToShow = ArrayList<Task>()

            // We filter the tasks based on the requestType
            for (task in result.data) {
                when (currentFiltering) {
                    TasksFilterType.ALL_TASKS -> tasksToShow.add(task)
                    TasksFilterType.ACTIVE_TASKS -> if (task.isActive) {
                        tasksToShow.add(task)
                    }
                    TasksFilterType.COMPLETED_TASKS -> if (task.isCompleted) {
                        tasksToShow.add(task)
                    }
                }
            }

            // The view may not be able to handle UI updates anymore
            if (tasksView.isActive) {
                if (showLoadingUI) {
                    tasksView.setLoadingIndicator(false)
                }

                processTasks(tasksToShow)
            }
        } else {
            // The view may not be able to handle UI updates anymore
            if (tasksView.isActive) {
                tasksView.showLoadingTasksError()
            }
        }

    }

    private fun processTasks(tasks: List<Task>) {
        if (tasks.isEmpty()) {
            // Show message indicating there no tasks for that filter type
            showEmptyTasks()
        } else {
            // Show list of tasks
            tasksView.showTasks(tasks)
            // Set the filter label's text.
            showFilterLabel()
        }
    }

    private fun showEmptyTasks() {
        when (currentFiltering) {
            TasksFilterType.ACTIVE_TASKS -> tasksView.showNoActiveTasks()
            TasksFilterType.COMPLETED_TASKS -> tasksView.showNoCompletedTasks()
            else -> tasksView.showNoTasks()
        }
    }

    private fun showFilterLabel() {
        when (currentFiltering) {
            TasksFilterType.COMPLETED_TASKS -> tasksView.showCompletedFilterLabel()
            TasksFilterType.ACTIVE_TASKS -> tasksView.showActiveFilterLabel()
            else -> tasksView.showAllFilterLabel()
        }
    }

}