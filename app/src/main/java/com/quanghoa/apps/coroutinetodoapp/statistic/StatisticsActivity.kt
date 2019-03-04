package com.quanghoa.apps.coroutinetodoapp.statistic

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.NavUtils
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.quanghoa.apps.coroutinetodoapp.Injection
import com.quanghoa.apps.coroutinetodoapp.R
import com.quanghoa.apps.coroutinetodoapp.util.replaceFragmentInActivity
import com.quanghoa.apps.coroutinetodoapp.util.setupActionBar

/**
 * Show statistics for tasks.
 */
class StatisticsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistics_act)

        // Set up the toolbar
        setupActionBar(R.id.toolbar) {
            setTitle(R.string.statistics_title)
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)

        }

        // Set up the navigation drawer
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout).apply {
            setStatusBarBackground(R.color.colorPrimaryDark)
        }

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        setupDrawerContent(navigationView)

        val statisticsFragment = supportFragmentManager.findFragmentById(R.id.contentFrame) as StatisticsFragment?
                ?: StatisticsFragment.newInstance().also {
                    replaceFragmentInActivity(it, R.id.contentFrame)
                }

        StatisticsPresenter(Injection.provideTasksRepository(applicationContext), statisticsFragment)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->

            if (menuItem.itemId == R.id.list_navigation_menu_item) {
                NavUtils.navigateUpFromSameTask(this@StatisticsActivity)
            }
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }
    }
}