package com.iqbal.app.mikrotikmonitor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_network_router_management.*

class NetworkRouterManagement : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_router_management)
        this.recyclerView.adapter = NetworkRouterListAdapter(ArrayList<NetworkRouter>())
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
            viewHolder.view.apply {
                // title.text = networkRouters[position].title
                // body.text = networkRouters[position].body
            }
        }
    }
}
