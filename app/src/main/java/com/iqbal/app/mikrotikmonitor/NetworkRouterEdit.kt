package com.iqbal.app.mikrotikmonitor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_network_router_edit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkRouterEdit : AppCompatActivity() {
    private var id: Int = -1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setUpView(savedInstanceState)
    }

    private fun setUpView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_network_router_edit)

        setFieldValues()
        setSupportActionBar(toolbar)

        updateButton.setOnClickListener {
            HttpService.instance.updateNetworkRouter(
                id,
                this.name.text.toString(),
                this.host.text.toString(),
                this.admin_username.text.toString(),
                this.admin_password.text.toString()
            ).enqueue(object : Callback<UpdateRouterResponse> {
                override fun onFailure(call: Call<UpdateRouterResponse>, t: Throwable) {
                    fail()
                }

                override fun onResponse(
                    call: Call<UpdateRouterResponse>,
                    response: Response<UpdateRouterResponse>
                ) {
                    if (response.code() != 200 || response.body() === null) {
                        fail()
                    }

                    success()
                }

                private fun success() {
                    Toast.makeText(this@NetworkRouterEdit, "Update berhasil", Toast.LENGTH_SHORT).show()
                }

                private fun fail() {
                    Toast.makeText(this@NetworkRouterEdit, "Terjadi masalah", Toast.LENGTH_SHORT).show()
                    setFieldValues()
                }
            })

        }
    }

    private fun setFieldValues() {
        intent.let {
            this.id = it.getIntExtra("id", -1)
            this.name.setText(it.getStringExtra("name"))
            this.host.setText(it.getStringExtra("host"))
            this.admin_username.setText(it.getStringExtra("admin_username"))
            this.admin_password.setText(it.getStringExtra("admin_password"))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_log_out -> {
            logOut()
        }
        R.id.action_router_menu -> {
            goToRouterManagement()
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun goToRouterManagement(): Boolean {
        startActivity(Intent(this, NetworkRouterManagement::class.java))
        return true
    }

    private fun logOut(): Boolean {
        with(Common.getPrimarySharedPreferences().edit()) {
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
        return true
    }
}
