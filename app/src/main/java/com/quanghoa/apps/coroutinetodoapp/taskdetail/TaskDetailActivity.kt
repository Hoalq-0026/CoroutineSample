package com.quanghoa.apps.coroutinetodoapp.taskdetail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.quanghoa.apps.coroutinetodoapp.Injection
import com.quanghoa.apps.coroutinetodoapp.R
import com.quanghoa.apps.coroutinetodoapp.util.replaceFragmentInActivity
import com.quanghoa.apps.coroutinetodoapp.util.setupActionBar

/**
 * Displays task details screen.
 */
class TaskDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taskdetail_act)

        setupActionBar(R.id.toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Get teh requested task id
        val taskId = intent.getStringExtra(EXTRA_TASK_ID)

        val taskDetailFragment = supportFragmentManager.findFragmentById(R.id.contentFrame) as TaskDetailFragment?
                ?: TaskDetailFragment.newInstance(taskId).also {
                    replaceFragmentInActivity(it, R.id.contentFrame)
                }

        // Create the presenter
        TaskDetailPresenter(taskId, Injection.provideTasksRepository(applicationContext), taskDetailFragment)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_TASK_ID = "TASK_ID"
    }
}