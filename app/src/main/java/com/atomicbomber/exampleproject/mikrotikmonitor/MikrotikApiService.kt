package com.atomicbomber.exampleproject.mikrotikmonitor

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

data class NetworkInterface(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val mtu: String = "",
    val actual_mtu: String = "",
    val l2mtu: String = "",
    val mac_address: String = "",
    val last_link_up_time: String = "",
    val link_downs: String = "",
    val rx_byte: String = "",
    val tx_byte: String = "",
    val rx_packet: String = "",
    val tx_packet: String = "",
    val rx_drop: String = "",
    val tx_drop: String = "",
    val tx_queue_drop: String = "",
    val rx_error: String = "",
    val tx_error: String = "",
    val fp_rx_byte: String = "",
    val fp_tx_byte: String = "",
    val fp_rx_packet: String = "",
    val fp_tx_packet: String = "",
    val running: String = "",
    val disabled: String = "",
    val comment: String = ""
) {

}

interface MikrotikApiService {
    @GET("/api/router_interface/{routerId}/index")
    fun getNetworkInterfaces(@Path("routerId")  routerId: Int): Call<List<NetworkInterface>>
}
