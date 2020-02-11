package com.iqbal.app.mikrotikmonitor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_user_log.*
import kotlinx.android.synthetic.main.user_log_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserLogFragment : AppFragment() {
    private val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
    private val userLogList: ArrayList<UserLog> = ArrayList()
    private val adapter: UserLogAdapter = UserLogAdapter(userLogList)

    override fun getLayout(): Int = R.layout.fragment_user_log

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()
        setUpView()
    }

    private fun setUpView() {
        this.recyclerView.layoutManager = layoutManager
        this.recyclerView.adapter = adapter

        this.swipeRefreshLayout.setOnRefreshListener {
            this.loadData()
        }
    }

    fun loadData() {
        userLogList.clear()
        adapter.notifyDataSetChanged()

        HttpService.instance.getUserLogs()
            .enqueue(object : Callback<UserLogIndexResponse> {
                override fun onFailure(call: Call<UserLogIndexResponse>, t: Throwable) {
                    fail("Response failed")
                }

                override fun onResponse(
                    call: Call<UserLogIndexResponse>,
                    response: Response<UserLogIndexResponse>
                ) {
                    when (true) {
                        !response.isSuccessful -> {
                            return fail("Response not successful")
                        }
                        response.body() === null -> {
                            return fail("Body is null")
                        }
                    }

                    response.body()?.let { userLogIndexResponse ->
                        userLogIndexResponse.data?.let {
                            userLogList.addAll(it)
                            adapter.notifyDataSetChanged()
                        }
                    }

                    finish()
                }

                private fun fail(message: String?) {
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }

                private fun finish(): Unit {
                    this@UserLogFragment.swipeRefreshLayout?.isRefreshing = false
                }
            })
    }

    class UserLogAdapter(private val userLogs: ArrayList<UserLog>) :
        RecyclerView.Adapter<UserLogAdapter.ViewHolder>() {

        class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.user_log_item, parent, false)
            )
        }

        override fun getItemCount(): Int = this.userLogs.size

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val userLog = userLogs[position]

            viewHolder.view.apply {
                log_id.text = userLog.id
                content.text = userLog.text
                time.text = userLog.created_at
                user_name.text = userLog.user?.username
            }
        }
    }
}