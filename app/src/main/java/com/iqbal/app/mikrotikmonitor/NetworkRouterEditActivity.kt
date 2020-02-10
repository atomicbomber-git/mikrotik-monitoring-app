package com.iqbal.app.mikrotikmonitor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_network_router_edit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkRouterEditActivity : AppCompatActivity() {
    private val menuHandler: MenuHandler = MenuHandler(this)
    private var id: Int = -1

    override fun onCreateOptionsMenu(menu: Menu?) = menuHandler.onCreateOptionsMenu(menu)
    override fun onOptionsItemSelected(item: MenuItem) = menuHandler.onOptionsItemSelected(item)

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
                    Toast.makeText(
                        this@NetworkRouterEditActivity,
                        "Update berhasil",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                private fun fail() {
                    Toast.makeText(
                        this@NetworkRouterEditActivity,
                        "Terjadi masalah",
                        Toast.LENGTH_SHORT
                    ).show()
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
}
