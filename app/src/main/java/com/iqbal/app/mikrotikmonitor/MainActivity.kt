package com.iqbal.app.mikrotikmonitor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!loggedIn(this)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setUpView()
    }

    private fun loggedIn(context: Context): Boolean {
        val token = context.getSharedPreferences(
            Config.SHARED_PREF_PRIMARY_ID,
            Context.MODE_PRIVATE
        ).getString(Config.SHARED_PREF_PRIMARY_KEY_API_TOKEN, null)

        return token !== null
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_log_out -> {
            with(getSharedPreferences(Config.SHARED_PREF_PRIMARY_ID, Context.MODE_PRIVATE).edit()) {
                remove(Config.SHARED_PREF_PRIMARY_KEY_API_TOKEN)
                commit()
            }

            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )
            finish()

            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

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


