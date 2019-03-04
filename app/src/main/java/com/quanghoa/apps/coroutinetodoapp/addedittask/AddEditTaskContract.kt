package com.quanghoa.apps.coroutinetodoapp.addedittask

import com.quanghoa.apps.coroutinetodoapp.BasePresenter
import com.quanghoa.apps.coroutinetodoapp.BaseView

interface AddEditTaskContract {

    interface View : BaseView<Presenter> {
        var isActive: Boolean

        fun showEmptyTaskError()

        fun showTaskList()

        fun setTitle(title: String)

        fun setDescription(description: String)
    }

    interface Presenter : BasePresenter {

        var isDataMissing: Boolean

        fun saveTask(title: String, description: String)

        fun populateTask()
    }
}