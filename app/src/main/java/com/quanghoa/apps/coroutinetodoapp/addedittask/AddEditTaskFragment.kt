package com.quanghoa.apps.coroutinetodoapp.addedittask

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.quanghoa.apps.coroutinetodoapp.R
import com.quanghoa.apps.coroutinetodoapp.util.showSnackBar

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */
class AddEditTaskFragment : Fragment(), AddEditTaskContract.View {

    override lateinit var presenter: AddEditTaskContract.Presenter

    override var isActive: Boolean = false
        get() = isAdded


    private lateinit var title: TextView
    private lateinit var description: TextView

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.findViewById<FloatingActionButton>(R.id.fab_edit_task_done)?.apply {
            setImageResource(R.drawable.ic_done)
            setOnClickListener {
                presenter.saveTask(title.text.toString(), description.text.toString())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.addtask_frag, container, false)
        with(root) {
            title = findViewById(R.id.add_task_title)
            description = findViewById(R.id.add_task_description)
        }
        setHasOptionsMenu(true)
        return root
    }

    override fun showEmptyTaskError() {
        title.showSnackBar(resources.getString(R.string.empty_task_message), Snackbar.LENGTH_LONG)
    }

    override fun showTaskList() {
        activity?.apply {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun setTitle(title: String) {
        this.title.text = title
    }

    override fun setDescription(description: String) {
        this.description.text = description
    }

    companion object {
        const val ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID"

        fun newInstance(taskId: String?) = AddEditTaskFragment().apply {
            arguments = Bundle().apply {
                putString(ARGUMENT_EDIT_TASK_ID, taskId)
            }
        }
    }

}