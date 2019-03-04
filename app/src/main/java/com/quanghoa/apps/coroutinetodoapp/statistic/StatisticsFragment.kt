package com.quanghoa.apps.coroutinetodoapp.statistic

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.quanghoa.apps.coroutinetodoapp.R
import kotlinx.android.synthetic.main.statistics_frag.view.*

class StatisticsFragment : Fragment(), StatisticsContract.View {

    private lateinit var statisticsTV: TextView

    override lateinit var presenter: StatisticsContract.Presenter

    override val isActive: Boolean
        get() = isAdded

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.statistics_frag, container, false)
        statisticsTV = root.findViewById(R.id.statistics)
        return root
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun setProgressIndicator(active: Boolean) {
        if (active) {
            statisticsTV.text = resources.getString(R.string.loading)
        } else {
            statisticsTV.text = ""
        }
    }

    override fun showStatistics(numberOfIncompleteTasks: Int, numberOfCompletedTasks: Int) {
        if (numberOfCompletedTasks == 0 && numberOfIncompleteTasks == 0) {
            statisticsTV.text = getString(R.string.statistics_no_tasks)
        } else {
            val displayString = "${resources.getString(R.string.statistics_active_tasks)} " +
                    "$numberOfIncompleteTasks\n" +
                    "${resources.getString(R.string.statistics_completed_tasks)} " +
                    "$numberOfCompletedTasks"
            statisticsTV.text = displayString
        }
    }

    override fun showLoadingStatisticsError() {
        statisticsTV.text = getString(R.string.statistics_error)
    }

    companion object {

        fun newInstance(): StatisticsFragment {
            return StatisticsFragment()
        }
    }

}