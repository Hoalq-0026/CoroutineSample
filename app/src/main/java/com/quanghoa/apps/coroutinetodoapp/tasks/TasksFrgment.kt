package com.quanghoa.apps.coroutinetodoapp.tasks

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.view.*
import android.widget.*
import com.quanghoa.apps.coroutinetodoapp.R
import com.quanghoa.apps.coroutinetodoapp.data.Task
import com.quanghoa.apps.coroutinetodoapp.util.showSnackBar
import kotlinx.android.synthetic.main.tasks_frag.view.*

class TasksFrgment : Fragment(), TasksContract.View {

    override lateinit var presenter: TasksContract.Presenter

    override var isActive: Boolean = false
        get() = isAdded

    private lateinit var noTasksView: View
    private lateinit var noTaskIcon: ImageView
    private lateinit var noTaskMainView: TextView
    private lateinit var noTaskAddView: TextView
    private lateinit var tasksView: LinearLayout
    private lateinit var filteringLabelView: TextView

    internal var itemClick: TaskItemListener = object : TaskItemListener {
        override fun onTaskClick(clickedTask: Task) {
            presenter.openTaskDetails(clickedTask)
        }

        override fun onCompleteTaskClick(completedTask: Task) {
            presenter.completeTask(completedTask)
        }

        override fun onActivateTaskClick(activatedTask: Task) {
            presenter.activateTask(activatedTask)
        }

    }

    private val listAdapter = TasksAdapter(ArrayList(0), itemClick)

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.result(requestCode, resultCode)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.tasks_frag, container, false)

        // Set up progress indicator
        with(root) {
            val listView = findViewById<ListView>(R.id.tasks_list).apply {
                adapter = listAdapter
            }
            // Set up progress indicator
            findViewById<ScrollChildSwipeRefreshLayout>(R.id.refresh_layout).apply {
                setColorSchemeColors(
                        ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                        ContextCompat.getColor(requireContext(), R.color.colorAccent),
                        ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
                )
                scrollUpChild = listView
                setOnRefreshListener { presenter.loadTasks(false) }
            }

            filteringLabelView = findViewById<TextView>(R.id.filteringLabel)
            tasksView = findViewById(R.id.tasksLL)

            // Set up no tasks view
            noTasksView = findViewById(R.id.notTasks)
            noTaskIcon = findViewById(R.id.noTasksIcon)
            noTaskMainView = findViewById(R.id.noTasksMain)
            noTaskAddView = findViewById<TextView>(R.id.noTasksAdd).also {
                it.setOnClickListener { showAddTask() }
            }
        }

        // set up floating action button
        activity?.findViewById<FloatingActionButton>(R.id.fab_add_task)?.apply {
            setImageResource(R.drawable.ic_add)
            setOnClickListener {
                presenter.addNewTask()
            }
        }

        setHasOptionsMenu(true)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.tasks_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear -> presenter.clearCompletedTask()
            R.id.menu_filter -> showFilteringPopUpMenu()
            R.id.menu_refresh -> presenter.loadTasks(true)
        }
        return true
    }

    override fun setLoadingIndicator(active: Boolean) {
        val root = view ?: return
        with(root.findViewById<SwipeRefreshLayout>(R.id.refresh_layout)) {
            // Make sure setRefreshing() is called after the layout is done with everything else.
            post { isRefreshing = active }
        }
    }

    override fun showTasks(tasks: List<Task>) {
        listAdapter.tasks = tasks
        tasksView.visibility = View.VISIBLE
        noTasksView.visibility = View.GONE
    }

    override fun showAddTask() {
        // TODO Later
    }

    override fun showTaskDetailsUi(taskId: String) {
        // TODO Later
    }

    override fun showTaskMarkedComplete() {
        showMessage(resources.getString(R.string.task_marked_complete))
    }

    override fun showTaskMarkedActive() {
        showMessage(resources.getString(R.string.task_marked_active))
    }

    override fun showCompletedTasksCleared() {
        showMessage(resources.getString(R.string.completed_tasks_cleared))
    }

    override fun showLoadingTasksError() {
        showMessage(getString(R.string.loading_tasks_error))
    }

    override fun showNoTasks() {
        showNoTasksView(resources.getString(R.string.no_tasks_all), R.drawable.ic_assignment_turned_in_24dp, false)
    }

    override fun showActiveFilterLabel() {
        filteringLabelView.text = resources.getString(R.string.label_active)
    }

    override fun showCompletedFilterLabel() {
        filteringLabelView.text = resources.getString(R.string.label_completed)
    }

    override fun showAllFilterLabel() {
        filteringLabelView.text = resources.getString(R.string.label_all)
    }

    override fun showNoActiveTasks() {
        showNoTasksView(resources.getString(R.string.no_tasks_active), R.drawable.ic_check_circle_24dp, false)
    }

    override fun showNoCompletedTasks() {
        showNoTasksView(resources.getString(R.string.no_tasks_completed), R.drawable.ic_verified_user_24dp, false)
    }

    override fun showSuccessullySavedMessage() {
        showMessage(resources.getString(R.string.successfully_saved_task_message))
    }

    override fun showFilteringPopUpMenu() {
        PopupMenu(requireContext(), requireActivity().findViewById(R.id.menu_filter))?.apply {
            menuInflater.inflate(R.menu.filter_tasks, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.active -> presenter.currentFiltering = TasksFilterType.ACTIVE_TASKS
                    R.id.complete -> presenter.currentFiltering = TasksFilterType.COMPLETED_TASKS
                    else -> presenter.currentFiltering = TasksFilterType.ALL_TASKS

                }
                presenter.loadTasks(false)
                true

            }
            show()
        }
    }

    private fun showMessage(message: String) {
        view?.showSnackBar(message, Snackbar.LENGTH_LONG)
    }

    private fun showNoTasksView(mainText: String, iconRes: Int, showAddView: Boolean) {
        tasksView.visibility = View.GONE
        noTasksView.visibility = View.VISIBLE

        noTaskMainView.text = mainText
        noTaskIcon.setImageResource(iconRes)
        noTaskAddView.visibility = if (showAddView) View.VISIBLE else View.GONE
    }

    private class TasksAdapter(tasks: List<Task>, private val itemListener: TaskItemListener)
        : BaseAdapter() {
        var tasks: List<Task> = tasks
            set(tasks) {
                field = tasks
                notifyDataSetChanged()
            }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val task = getItem(position)

            val rowView = convertView ?: LayoutInflater.from(parent.context)
                    .inflate(R.layout.task_item, parent, false)
            with(rowView.findViewById<TextView>(R.id.title)) {
                text = task.titleForList
            }

            with(rowView.findViewById<CheckBox>(R.id.complete)) {
                //Active/completed task UI
                isChecked = task.isCompleted
                val rowViewBackground =
                        if (task.isCompleted) R.drawable.list_completed_touch_feedback
                        else R.drawable.touch_feedback

                rowView.setBackgroundResource(rowViewBackground)
                setOnClickListener {
                    if (!task.isCompleted) {
                        itemListener.onCompleteTaskClick(task)
                    } else {
                        itemListener.onActivateTaskClick(task)
                    }
                }
            }

            rowView.setOnClickListener { itemListener.onTaskClick(task) }
            return rowView
        }

        override fun getItem(position: Int) = tasks[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getCount(): Int = tasks.size

    }

    interface TaskItemListener {

        fun onTaskClick(clickedTask: Task)

        fun onCompleteTaskClick(completedTask: Task)

        fun onActivateTaskClick(activatedTask: Task)
    }

    companion object {
        fun newInstance() = TasksFrgment()
    }
}