package com.iqbal.app.mikrotikmonitor

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_network_router_management.*
import kotlinx.android.synthetic.main.network_router_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkRouterManagementActivity : AppCompatActivity() {
    private val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
    private var networkRouterList: ArrayList<NetworkRouter> = ArrayList()
    private val adapter = NetworkRouterListAdapter(networkRouterList, this)
    private val menuHandler: MenuHandler = MenuHandler(this)

    override fun onCreateOptionsMenu(menu: Menu?) = menuHandler.onCreateOptionsMenu(menu)
    override fun onOptionsItemSelected(item: MenuItem) = menuHandler.onOptionsItemSelected(item)

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
                    Toast.makeText(this@NetworkRouterManagementActivity, t.message, Toast.LENGTH_LONG)
                        .show()
                }

                override fun onResponse(
                    call: Call<List<NetworkRouter>>,
                    response: Response<List<NetworkRouter>>
                ) {
                    loadDataFinished()
                    if (response.code() != 200 && response.body() === null) {
                        Toast.makeText(
                            this@NetworkRouterManagementActivity,
                            response.message(),
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }

                    response.body()?.apply {
                        networkRouterList.addAll(this)
                        adapter.notifyDataSetChanged()
                    }
                }
            })
    }

    class NetworkRouterListAdapter(private val networkRouters: ArrayList<NetworkRouter>, val activity: AppCompatActivity) :
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
                host.text = networkRouter.host
                adminUsername.text = networkRouter.admin_username
                adminPassword.text = networkRouter.admin_password

                update_button.setOnClickListener {
                    val intent = Intent(Common.appContext, NetworkRouterEditActivity::class.java)

                    intent.putExtra("id", networkRouter.id)
                    intent.putExtra("name", networkRouter.name)
                    intent.putExtra("host", networkRouter.host)
                    intent.putExtra("admin_username", networkRouter.admin_username)
                    intent.putExtra("admin_password", networkRouter.admin_password)

                    activity.startActivity(intent)
                }
            }
        }
    }
}
