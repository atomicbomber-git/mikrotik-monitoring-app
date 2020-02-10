package com.iqbal.app.mikrotikmonitor

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

class MenuHandler(val activity: AppCompatActivity) {
    fun onCreateOptionsMenu(menu: Menu?): Boolean {
        activity.menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_log_out -> {
            logOut()
        }
        R.id.action_router_menu -> {
            goToRouterManagement()
        }
        else -> {
            activity.onOptionsItemSelected(item)
        }
    }

    fun goToRouterManagement(): Boolean {
        activity.startActivity(
            Intent(
                activity,
                NetworkRouterManagementActivity::class.java
            )
        )
        return true
    }

    fun logOut(): Boolean {
        with(Common.getPrimarySharedPreferences().edit()) {
            remove(Config.SHARED_PREF_PRIMARY_KEY_API_TOKEN)
            commit()
        }

        this.activity.startActivity(
            Intent(
                this.activity,
                MainActivity::class.java
            )
        )
        this.activity.finish()
        return true
    }
}