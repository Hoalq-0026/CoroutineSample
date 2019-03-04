package com.quanghoa.apps.coroutinetodoapp.tasks

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.quanghoa.apps.coroutinetodoapp.Injection
import com.quanghoa.apps.coroutinetodoapp.R
import com.quanghoa.apps.coroutinetodoapp.util.replaceFragmentInActivity
import com.quanghoa.apps.coroutinetodoapp.util.setupActionBar

class TasksActivity : AppCompatActivity() {
    private val CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY"

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var tasksPresenter: TasksPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_act)

        // Set up toolbar
        setupActionBar(R.id.toolbar) {
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }

        // Set up the navigation drawer.
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout).apply {
            setStatusBarBackground(R.color.colorPrimaryDark)
        }
        setupDrawerContent(findViewById<NavigationView>(R.id.nav_view))

        val tasksFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
                as TasksFrgment? ?: TasksFrgment.newInstance().also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }

        // Create the presenter
        tasksPresenter = TasksPresenter(Injection.provideTasksRepository(applicationContext), tasksFragment).apply {
            // Load previously saved state, if available
            if (savedInstanceState != null) {
                currentFiltering = savedInstanceState.get(CURRENT_FILTERING_KEY)
                        as TasksFilterType
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState.apply {
            putSerializable(CURRENT_FILTERING_KEY, tasksPresenter.currentFiltering)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // Open the navigation drawer when the home icon is selected from the toolbar
            drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.statistic_navigation_menu_item) {
                // Todo later
            }

            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }
    }
}