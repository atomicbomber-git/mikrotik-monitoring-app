package com.iqbal.app.mikrotikmonitor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_logs.*
import kotlinx.android.synthetic.main.log_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RouterLogFragment(): AppFragment() {
    private val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
    private val routerLogList: ArrayList<RouterLog> = ArrayList()
    private val adapter: LogListAdapter = LogListAdapter(routerLogList)

    override fun getLayout() = R.layout.fragment_logs

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        log_index_swipe_refresh_layout.setOnRefreshListener {
            loadData()
        }

        setUpRecyclerView()
        loadData()
    }

    private fun setUpRecyclerView() {
        log_index_recycler_view.layoutManager = this.layoutManager
        log_index_recycler_view.adapter = adapter
    }

    private fun loadDataFinished() {
        log_index_swipe_refresh_layout?.apply {
            isRefreshing = false
        }
    }

    private fun loadData() {
        HttpService.instance.getLogs(Config.PRIMARY_ROUTER_ID)
            .enqueue(object: Callback<List<RouterLog>> {
                override fun onFailure(call: Call<List<RouterLog>>, t: Throwable) {
                    loadDataFinished()

                    t.message?.apply {
                        fail(this)
                        return
                    }
                    fail("Unknown Error.")
                }

                override fun onResponse(call: Call<List<RouterLog>>, response: Response<List<RouterLog>>) {

                    loadDataFinished()

                    if (response.code() != 200 && response.body() === null) {
                        fail(response.message())
                        return
                    }

                    routerLogList.also {
                        response.body()?.also {responseLogList ->
                            routerLogList.clear()
                            routerLogList.addAll(responseLogList)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }

                private fun fail(message: String) {
                    context?.run {
                        Toast.makeText(this, message, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            })
    }

    class LogListAdapter(private val accessListItems: ArrayList<RouterLog>): RecyclerView.Adapter<LogListAdapter.ViewHolder>() {

        class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, position: Int) = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.log_item, parent, false)
        )

        override fun getItemCount(): Int = this.accessListItems.size

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.view.apply {
                accessListItems[position].also { log ->
                    log_id.text = log.id
                    log_time.text = log.time
                    log_topic.text = log.topics
                    log_message.text = log.message
                }
            }
        }
    }
}