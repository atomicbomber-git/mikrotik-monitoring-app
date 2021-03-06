package com.iqbal.app.mikrotikmonitor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val menuHandler: MenuHandler = MenuHandler(this)

    override fun onCreateOptionsMenu(menu: Menu?) = menuHandler.onCreateOptionsMenu(menu)
    override fun onOptionsItemSelected(item: MenuItem) = menuHandler.onOptionsItemSelected(item)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!loggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setUpView()
    }

    private fun loggedIn() = Common.getApiToken() !== null

    private fun setUpView() {
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val demoCollectionAdapter = MainActivityFragmentAdapter(this)
        main_activity_view_pager.adapter = demoCollectionAdapter

        TabLayoutMediator(main_activity_tab_layout, main_activity_view_pager) { tab, position ->
            tab.text = Feature.values()[position].tabName()
        }.attach()
    }
}


