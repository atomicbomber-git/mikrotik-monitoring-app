package com.iqbal.app.mikrotikmonitor

import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_router_traffic.*
import kotlinx.android.synthetic.main.fragment_user_log.*
import kotlinx.android.synthetic.main.fragment_user_log.swipeRefreshLayout
import kotlinx.android.synthetic.main.user_log_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToLong
import java.util.TimerTask
import java.util.Timer



class RouterTrafficFragment : AppFragment() {
    override fun getLayout(): Int = R.layout.fragment_router_traffic
    private var isLoading: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        setUpView()

        val period: Long = 1000
        Timer().schedule(object : TimerTask() {
            override fun run() {
                loadData()
            }
        }, 0, period)
    }

    private fun setUpView() {
        this.swipeRefreshLayout?.setOnRefreshListener {
            this.loadData()
        }
    }

    private fun onLoadFinish() {
        this.swipeRefreshLayout?.isRefreshing = false
        this.isLoading = false
    }

    private fun loadData() {
        if (isLoading) {
            return
        }

        this.isLoading = true

        HttpService.instance.getRouterTrafficData(Config.PRIMARY_ROUTER_ID)
            .enqueue(object : Callback<RouterTrafficData> {
                override fun onFailure(call: Call<RouterTrafficData>, t: Throwable) {
                    fail()
                }

                override fun onResponse(
                    call: Call<RouterTrafficData>,
                    response: Response<RouterTrafficData>
                ) {
                    if (!response.isSuccessful) {
                        fail()
                    }

                    response.body()?.let {routerData ->
                        this@RouterTrafficFragment.download_speed?.text = Formatter.formatFileSize(Common.appContext, routerData.rx_bits_per_second.toLong() / 8) + " per second"
                        this@RouterTrafficFragment.upload_speed?.text = Formatter.formatFileSize(Common.appContext, routerData.tx_bits_per_second.toLong() / 8) + " per second"
                    }

                    onLoadFinish()
                }

                private fun fail() {
                    activity?.let {
                        Toast.makeText(it, "Gagal menghubungi server", Toast.LENGTH_SHORT)
                            .show()
                    }

                    onLoadFinish()
                }
            })
    }
}