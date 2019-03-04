package com.quanghoa.apps.coroutinetodoapp.statistic

import com.quanghoa.apps.coroutinetodoapp.data.source.Result
import com.quanghoa.apps.coroutinetodoapp.data.source.TasksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Listeners to user actions from the UI, retrieves the data and updates
 * the UI as required.
 */
class StatisticsPresenter(
        private val tasksRepository: TasksRepository,
        private val statisticsView: StatisticsContract.View,
        private val uiContext: CoroutineContext = Dispatchers.Main
) : StatisticsContract.Presenter {

    init {
        statisticsView.presenter = this
    }

    override fun start() {
        loadStatistics()
    }

    private fun loadStatistics() = GlobalScope.launch(uiContext) {
        statisticsView.setProgressIndicator(true)

        val result = tasksRepository.getTasks()
        if (result is Result.Success) {
            // We calculate number of active and completed tasks
            val completedTasks = result.data.filter { it.isCompleted }.size
            val activeTasks = result.data.size - completedTasks

            if (statisticsView.isActive) {
                statisticsView.setProgressIndicator(false)
                statisticsView.showStatistics(completedTasks, activeTasks)
            }
        } else {
            if (statisticsView.isActive) {
                statisticsView.showLoadingStatisticsError()
            }
        }
    }

}