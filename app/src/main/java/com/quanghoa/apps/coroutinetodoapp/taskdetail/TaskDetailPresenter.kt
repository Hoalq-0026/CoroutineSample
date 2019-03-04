package com.quanghoa.apps.coroutinetodoapp.taskdetail

import com.quanghoa.apps.coroutinetodoapp.data.Task
import com.quanghoa.apps.coroutinetodoapp.data.source.Result
import com.quanghoa.apps.coroutinetodoapp.data.source.TasksRepository
import com.quanghoa.apps.coroutinetodoapp.util.launchSilent
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class TaskDetailPresenter(
        private val taskId: String,
        private val tasksRepository: TasksRepository,
        private val taskDetailView: TaskDetailContract.View,
        private val uiContext: CoroutineContext = Dispatchers.Main
) : TaskDetailContract.Presenter {

    init {
        taskDetailView.presenter = this
    }

    override fun editTask() {
        if (taskId.isEmpty()) {
            taskDetailView.showMissingTask()
            return
        }
        taskDetailView.showEditTask(taskId)
    }

    override fun deleteTask() = launchSilent(uiContext) {
        if (taskId.isEmpty()) {
            taskDetailView.showMissingTask()
        } else {
            tasksRepository.deleteTask(taskId)
            taskDetailView.showTaskDeleted()
        }
    }

    override fun completeTask() = launchSilent(uiContext) {
        if (taskId.isEmpty()) {
            taskDetailView.showMissingTask()
        } else {
            tasksRepository.completeTask(taskId)
            taskDetailView.showTaskMarkedComplete()
        }
    }

    override fun activateTask() = launchSilent(uiContext) {
        if (taskId.isEmpty()) {
            taskDetailView.showMissingTask()
        } else {
            tasksRepository.activateTask(taskId)
            taskDetailView.showTaskMarkedActive()
        }
    }

    override fun start() {
        openTask()
    }

    private fun openTask() = launchSilent(uiContext) {
        if (taskId.isEmpty()) {
            taskDetailView.showMissingTask()
        } else {
            taskDetailView.setLoadingIndicator(true)
            val result = tasksRepository.getTask(taskId)

            if (taskDetailView.isActive) {
                if (result is Result.Success) {
                    taskDetailView.setLoadingIndicator(false)
                    showTask(result.data)
                } else {
                    taskDetailView.showMissingTask()
                }
            }
        }
    }

    private fun showTask(task: Task) {
        with(taskDetailView) {
            if (taskId.isEmpty()) {
                hideTitle()
                hideDescription()
            } else {
                showTitle(task.title)
                showDescription(task.description)
            }
            showCompletionStatus(task.isCompleted)
        }
    }

}