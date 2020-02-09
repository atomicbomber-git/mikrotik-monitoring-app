package com.iqbal.app.mikrotikmonitor

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_network_router_management.*
import kotlinx.android.synthetic.main.activity_network_router_management.toolbar
import kotlinx.android.synthetic.main.network_router_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface MenuActivity {
    fun onCreateOptionsMenu(menu: Menu?): Boolean
    fun onOptionsItemSelected(item: MenuItem): Boolean
    fun logOut(): Boolean
    fun goToRouterManagement(): Boolean
}

class MenuActivityImpl (val activity: AppCompatActivity) : MenuActivity {
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        activity.menuInflater.inflate(R.menu.menu, menu)
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
            activity.onOptionsItemSelected(item)
        }
    }

    override fun logOut(): Boolean {
        return true
    }

    override fun goToRouterManagement(): Boolean {
        return true
    }
}

class NetworkRouterManagement : AppCompatActivity() {
    private val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
    private var networkRouterList: ArrayList<NetworkRouter> = ArrayList()
    private val adapter = NetworkRouterListAdapter(networkRouterList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpView()
        loadData()
    }

    private fun setUpView() {
        setContentView(R.layout.activity_network_router_management)
        setSupportActionBar(toolbar)

        this.recyclerView.layoutManager = layoutManager
        this.recyclerView.adapter = adapter
        this.swipeRefreshLayout?.setOnRefreshListener { loadData() }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
//        R.id.action_log_out -> {
//            logOut()
//        }
//        R.id.action_router_menu -> {
//            goToRouterManagement()
//        }
//        else -> {
//            super.onOptionsItemSelected(item)
//        }
//    }
//
//    override fun goToRouterManagement(): Boolean {
//        startActivity(Intent(this, NetworkRouterManagement::class.java))
//        return true
//    }
//
//    override fun logOut(): Boolean {
//        with(Common.getPrimarySharedPreferences().edit()) {
//            remove(Config.SHARED_PREF_PRIMARY_KEY_API_TOKEN)
//            commit()
//        }
//
//        startActivity(
//            Intent(
//                this,
//                MainActivity::class.java
//            )
//        )
//        finish()
//        return true
//    }

    private fun loadDataFinished() {
        this.swipeRefreshLayout?.isRefreshing = false
    }

    private fun loadData() {
        this.swipeRefreshLayout?.isRefreshing = true
        networkRouterList.clear()
        adapter.notifyDataSetChanged()

        HttpService.instance.getNetworkRouters()
            .enqueue(object : Callback<List<NetworkRouter>> {
                override fun onFailure(call: Call<List<NetworkRouter>>, t: Throwable) {
                    loadDataFinished()
                    Toast.makeText(this@NetworkRouterManagement, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<List<NetworkRouter>>,
                    response: Response<List<NetworkRouter>>
                ) {
                    loadDataFinished()
                    if (response.code() != 200 && response.body() === null) {
                        Toast.makeText(this@NetworkRouterManagement, response.message(), Toast.LENGTH_LONG).show()
                        return
                    }

                    response.body()?.apply {
                        networkRouterList.addAll(this)
                        adapter.notifyDataSetChanged()
                    }
                }
            })
    }

    class NetworkRouterListAdapter(private val networkRouters: ArrayList<NetworkRouter>) :
        RecyclerView.Adapter<NetworkRouterListAdapter.ViewHolder>() {

        class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.network_router_item,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int = this.networkRouters.size

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val networkRouter = networkRouters[position]
            viewHolder.view.apply {
                name.text = networkRouter.name
                host.text= networkRouter.host
                adminUsername.text = networkRouter.admin_username
                adminPassword.text = networkRouter.admin_password
            }
        }
    }
}
