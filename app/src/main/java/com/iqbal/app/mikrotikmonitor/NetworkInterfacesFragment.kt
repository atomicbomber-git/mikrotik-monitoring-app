package com.iqbal.app.mikrotikmonitor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_network_interface.*
import kotlinx.android.synthetic.main.interface_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkInterfacesFragment : AppFragment() {
    override fun getLayout() = R.layout.fragment_network_interface
    val networkInterfaceList = ArrayList<NetworkInterface>()
    val adapter = NetworkInterfaceListAdapter(networkInterfaceList)
    private lateinit var apiToken: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Common.getApiToken().let { apiToken ->
            if (apiToken === null) {
                goToLoginActivity()
                return
            }

            this.apiToken = apiToken
        }

        this.swipeRefreshLayout.setOnRefreshListener {
            loadData()
        }

        setUpInterfaceRecyclerView()
        loadData()
    }

    private fun goToLoginActivity() {
        startActivity(Intent(Common.appContext, LoginActivity::class.java))
        activity?.finish()
    }

    private fun loadDataFinished() {
        swipeRefreshLayout?.isRefreshing = false
    }

    private fun setUpInterfaceRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
        }
        recyclerView.adapter = adapter
    }

    private fun loadData() {
        networkInterfaceList.clear()
        adapter.notifyDataSetChanged()

        HttpService.instance.getNetworkInterfaces(Config.PRIMARY_ROUTER_ID, this.apiToken)
            .enqueue(object : Callback<List<NetworkInterface>> {
                override fun onFailure(call: Call<List<NetworkInterface>>, t: Throwable) {
                    loadDataFinished()
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG)
                        .show()
                }

                override fun onResponse(
                    call: Call<List<NetworkInterface>>,
                    response: Response<List<NetworkInterface>>
                ) {
                    loadDataFinished()

                    if (response.code() == 401) {
                        goToLoginActivity()
                        return
                    }

                    if (!response.isSuccessful && response.body() === null) {
                        Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show()
                        return
                    }

                    response.body().apply {
                        networkInterfaceList
                            .addAll(this as ArrayList<NetworkInterface>)
                        adapter.notifyDataSetChanged()
                    }
                }
            })
    }

    inner class NetworkInterfaceListAdapter(private val dataset: MutableList<NetworkInterface>) :
        RecyclerView.Adapter<NetworkInterfaceListAdapter.ItemViewHolder>() {

        inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.interface_item, parent, false)

            return ItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

            val networkInterface = this.dataset[position]

            holder.itemView.apply {
                interface_id.text = networkInterface.id
                name.text = networkInterface.name

                disabled.apply {
                    text = if (networkInterface.disabled == "false") "False" else "True"
                    setTextColor(
                        if (networkInterface.disabled == "true")
                            resources.getColor(R.color.colorPrimary, Common.appContext.theme)
                        else
                            resources.getColor(R.color.colorAccent, Common.appContext.theme)
                    )
                }
                button_toggle_disabled.apply {
                    text = if (networkInterface.disabled == "false") "Disable" else "Enable"
                }
            }

            holder.itemView.button_toggle_disabled.setOnClickListener {
                toggleNetworkInterface(networkInterface.id) { networkInterface ->
                    Toast.makeText(activity, getString(R.string.ACTION_SUCCESS), Toast.LENGTH_SHORT)
                        .show()
                    dataset[position] = networkInterface
                    notifyDataSetChanged()
                }
            }
        }

        private fun toggleNetworkInterface(
            networkInterfaceId: String,
            onSuccess: (networkInterface: NetworkInterface) -> Unit
        ) {
            startRefreshing()

            HttpService.instance.toggleNetworkInterface(
                Config.PRIMARY_ROUTER_ID,
                networkInterfaceId,
                this@NetworkInterfacesFragment.apiToken
            )
                .enqueue(object : Callback<NetworkInterface> {
                    override fun onFailure(call: Call<NetworkInterface>, t: Throwable) {
                        stopRefreshing()

                        Toast.makeText(
                            activity,
                            getString(R.string.connection_problem),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onResponse(
                        call: Call<NetworkInterface>,
                        response: Response<NetworkInterface>
                    ) {
                        stopRefreshing()

                        response.body()?.let {
                            onSuccess(it)
                        }

                        if (response.body() === null) {
                            Toast.makeText(
                                activity,
                                getString(R.string.connection_problem),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        }

        override fun getItemCount() = dataset.size
    }

    private fun stopRefreshing() {
        this@NetworkInterfacesFragment.swipeRefreshLayout?.isRefreshing = false
    }

    private fun startRefreshing() {
        this@NetworkInterfacesFragment.swipeRefreshLayout?.isRefreshing = true
    }
}