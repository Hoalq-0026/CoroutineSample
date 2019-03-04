package com.quanghoa.apps.coroutinetodoapp.taskdetail

import com.quanghoa.apps.coroutinetodoapp.BasePresenter
import com.quanghoa.apps.coroutinetodoapp.BaseView

interface TaskDetailContract {

    interface View : BaseView<Presenter> {

        var isActive: Boolean

        fun setLoadingIndicator(active: Boolean)

        fun showMissingTask()

        fun hideTitle()

        fun showTitle(title: String)

        fun hideDescription()

        fun showDescription(description: String)

        fun showCompletionStatus(complete: Boolean)

        fun showEditTask(taskId: String)

        fun showTaskDeleted()

        fun showTaskMarkedComplete()

        fun showTaskMarkedActive()
    }

    interface Presenter : BasePresenter {

        fun editTask()

        fun deleteTask()

        fun completeTask()

        fun activateTask()
    }
}