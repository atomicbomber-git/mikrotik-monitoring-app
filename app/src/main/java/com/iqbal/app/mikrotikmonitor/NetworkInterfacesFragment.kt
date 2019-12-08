package com.iqbal.app.mikrotikmonitor

import android.content.Context
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

class NetworkInterfacesFragment: AppFragment() {
    override fun getLayout() = R.layout.fragment_network_interface
    val networkInterfaceList = ArrayList<NetworkInterface>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.interface_index_swipe_refresh_layout.setOnRefreshListener {
            loadData()
        }

        setUpInterfaceRecyclerView()
        loadData()
    }

    private fun loadDataFinished() {
        interface_index_swipe_refresh_layout.isRefreshing = false
    }

    private fun setUpInterfaceRecyclerView() {
        interface_index_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = NetworkInterfaceListAdapter(networkInterfaceList)
        }
    }

    private fun loadData() {
        networkInterfaceList.clear()

        HttpService.instance.getNetworkInterfaces(Config.PRIMARY_ROUTER_ID)
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
                    if (response.code() != 200 && response.body() === null) {
                        Toast.makeText(context, response.message(), Toast.LENGTH_LONG)
                            .show()
                        return
                    }

                    response.body().apply {
                        networkInterfaceList
                            .addAll(this as ArrayList<NetworkInterface>)
                        interface_index_recycler_view.adapter?.notifyDataSetChanged()
                    }
                }
            })
    }

    inner class NetworkInterfaceListAdapter(private val dataset: List<NetworkInterface>) :
        RecyclerView.Adapter<NetworkInterfaceListAdapter.ItemViewHolder>() {

        inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): ItemViewHolder {
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
                            resources.getColor(R.color.colorPrimary)
                        else
                            resources.getColor(R.color.colorAccent)
                    )
                }
                button_toggle_disabled.apply {
                    text = if (networkInterface.disabled == "false") "Disable" else "Enable" }
            }

            holder.itemView.button_toggle_disabled.setOnClickListener {
                toggleNetworkInterface(networkInterface.id)
            }
        }

        private fun toggleNetworkInterface(networkInterfaceId: String) {
            HttpService.instance.toggleNetworkInterface(
                Config.PRIMARY_ROUTER_ID,
                networkInterfaceId
            )
                .enqueue(object: Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {
                    }

                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        loadData()
                    }
                })
        }

        override fun getItemCount() = dataset.size
    }
}