package com.iqbal.app.mikrotikmonitor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.connected_client_item.view.*
import kotlinx.android.synthetic.main.fragment_connected_clients.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface connectedClientBanListener {
    public fun onConnectedClientBan()
}

class ConnectedClientsFragment: AppFragment(), connectedClientBanListener {
    private val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
    private val connectedClientList: ArrayList<ConnectedClient> = ArrayList()
    private val adapter: ConnectedClientListAdapter = ConnectedClientListAdapter(connectedClientList, this)

    override fun getLayout() = R.layout.fragment_connected_clients

    override fun onConnectedClientBan() {
        loadData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        connected_client_index_swipe_refresh_layout.setOnRefreshListener {
            this.loadData()
        }

        connected_client_index_recycler_view.let {recyclerView ->
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }

        loadData()
    }

    private fun loadData() {
        connected_client_index_swipe_refresh_layout.isRefreshing = true

        HttpService.instance.getConnectedClients(Config.PRIMARY_ROUTER_ID)
            .enqueue(object: Callback<List<ConnectedClient>> {
                override fun onFailure(call: Call<List<ConnectedClient>>, t: Throwable) {
                    onLoadingFinished()
                }

                override fun onResponse(
                    call: Call<List<ConnectedClient>>,
                    response: Response<List<ConnectedClient>>
                ) {
                    onLoadingFinished()
                    response.body()?.run {
                        connectedClientList.clear()
                        connectedClientList.addAll(this)
                        adapter.notifyDataSetChanged()
                    }
                }
            })
    }

    private fun onLoadingFinished() {
        connected_client_index_swipe_refresh_layout.isRefreshing = false
    }

    class ConnectedClientListAdapter(private val connectedClients: ArrayList<ConnectedClient>, private val connectedClientBanListener: connectedClientBanListener):
        RecyclerView.Adapter<ConnectedClientListAdapter.ViewHolder>() {
        
        class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, position: Int) = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.connected_client_item, parent, false)
        )

        override fun getItemCount(): Int = this.connectedClients.size

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.view.let { view ->
                connectedClients[position].let { connectedClient ->
                    view.client_id.text = connectedClient.id
                    view.network_interface.text = connectedClient.network_interface
                    view.mac_address.text = connectedClient.mac_address
                    view.access_point.text = connectedClient.ap

                    // Handle ban button click action
                    view.banConnectedClientButton.setOnClickListener {

                        Log.d("RESPONSE_DEBUG", "Experiment")

                        HttpService.instance.createAccessListItem(Config.PRIMARY_ROUTER_ID, AccessListItem(
                            mac_address = connectedClient.mac_address,
                            authentication = "no",
                            network_interface = connectedClient.network_interface
                        ))
                            .enqueue(object: Callback<CommandResponse> {
                                override fun onFailure(call: Call<CommandResponse>, t: Throwable) {
                                    Log.d("RESPONSE_DEBUG", t.message)
                                }

                                override fun onResponse(
                                    call: Call<CommandResponse>,
                                    response: Response<CommandResponse>
                                ) {
                                    if (response.code() != 200) {
                                        fail(response.message())
                                        return
                                    }

                                    connectedClientBanListener.onConnectedClientBan()
                                }

                                private fun fail(message: String? = Common.appContext.getString(R.string.ACTION_FAILED)) {
                                    Toast.makeText(Common.appContext, message, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            })
                    }
                }
            }
        }
    }
}