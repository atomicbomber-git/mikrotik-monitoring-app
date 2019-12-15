package com.iqbal.app.mikrotikmonitor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_connected_clients.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConnectedClientsFragment: AppFragment() {
    private val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
    private val connectedClientList: ArrayList<ConnectedClient> = ArrayList()
    private val adapter: LogsFragment.LogListAdapter = LogsFragment.LogListAdapter(connectedClientList)

    override fun getLayout() = R.layout.fragment_connected_clients

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        connected_client_index_swipe_refresh_layout.setOnRefreshListener {
            this.loadData()
        }
    }

    private fun loadData() {
        HttpService.instance.getConnectedClients(Config.PRIMARY_ROUTER_ID)
            .enqueue(object: Callback<List<ConnectedClient>> {
                override fun onFailure(call: Call<List<ConnectedClient>>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<List<ConnectedClient>>,
                    response: Response<List<ConnectedClient>>
                ) {
                }
            })
    }

    private fun onLoadingFinished() {

    }

    class ConnectedClientListAdapter(private val connectedClients: ArrayList<ConnectedClient>):
            RecyclerView.Adapter<ConnectedClientListAdapter.ViewHolder>() {

        class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, position: Int) = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.connected_client_item, parent, false)
        )

        override fun getItemCount(): Int = this.connectedClients.size

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.view.apply {
                /
            }
        }
    }
}