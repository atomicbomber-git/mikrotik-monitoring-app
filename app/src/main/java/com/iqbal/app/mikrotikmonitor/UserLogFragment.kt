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

        this.swipeRefreshLayout.setOnRefreshListener {
            this.loadData()
        }
    }

    fun loadData() {
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
                            fail("Response not successful"); return; }
                        response.body() === null -> {
                            fail("Body is null"); return; }
                    }

                    response.body()?.let {
                        it.data?.forEach { userLog ->
                            Log.d("PERSONAL_DEBUG_LOG", userLog.text)
                        }
                    }
                }

                private fun fail(message: String?) {
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT)
                        .show()
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
            viewHolder.view.apply {
                // title.text = userLogs[position].title
                // body.text = userLogs[position].body
            }
        }
    }
}