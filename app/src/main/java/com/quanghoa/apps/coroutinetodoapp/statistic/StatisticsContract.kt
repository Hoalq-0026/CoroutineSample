package com.quanghoa.apps.coroutinetodoapp.statistic

import com.quanghoa.apps.coroutinetodoapp.BasePresenter
import com.quanghoa.apps.coroutinetodoapp.BaseView

interface StatisticsContract {

    interface View : BaseView<Presenter> {
        val isActive: Boolean

        fun setProgressIndicator(active: Boolean)

        fun showStatistics(numberOfIncompleteTasks: Int, numberOfCompletedTasks: Int)

        fun showLoadingStatisticsError()
    }

    interface Presenter : BasePresenter
}