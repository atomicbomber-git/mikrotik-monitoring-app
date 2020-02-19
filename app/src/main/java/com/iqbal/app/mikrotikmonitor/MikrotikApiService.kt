package com.iqbal.app.mikrotikmonitor

import retrofit2.Call
import retrofit2.http.*

data class RouterLog(
    val id: String = "",
    val time: String = "",
    val topics: String = "",
    val message: String = ""
)

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
)

data class ConnectedClient(
    val id: String = "",
    val network_interface: String = "",
    val mac_address: String = "",
    val ap: String = "",
    val wds: String = "",
    val bridge: String = "",
    val rx_rate: String = "",
    val tx_rate: String = "",
    val packets: String = "",
    val bytes: String = "",
    val frames: String = "",
    val frame_bytes: String = "",
    val hw_frames: String = "",
    val hw_frame_bytes: String = "",
    val tx_frames_timed_out: String = "",
    val uptime: String = "",
    val last_activity: String = "",
    val signal_strength: String = "",
    val signal_to_noise: String = "",
    val signal_strength_ch0: String = "",
    val signal_strength_ch1: String = "",
    val strength_at_rates: String = "",
    val tx_ccq: String = "",
    val p_throughput: String = "",
    val last_ip: String = "",
    val _8021x_port_enabled: String = "",
    val management_protection: String = "",
    val wmm_enabled: String = "",
    val tx_rate_set: String = ""
)

data class AccessListItem(
    val id: String = "",
    val mac_address: String = "",
    val network_interface: String = "",
    val signal_range: String = "",
    val allow_signal_out_of_range: String = "",
    val authentication: String = "",
    val forwarding: String = "",
    val ap_tx_limit: String = "",
    val client_tx_limit: String = "",
    val private_algo: String = "",
    val private_key: String = "",
    val private_pre_shared_key: String = "",
    val management_protection_key: String = "",
    val vlan_mode: String = "",
    val vlan_id: String = "",
    val disabled: String = ""
)

data class NetworkRouter(
    val id: Int = -1,
    val user_id: Int = -1,
    val name: String = "",
    val host: String = "",
    val admin_username: String = "",
    val admin_password: String = "",
    val is_primary: String = "",
    val created_at: String = "",
    val updated_at: String = ""
)

data class RouterTrafficData (
    val rx_packets_per_second: Int = -1,
    val rx_bits_per_second: Int = -1,
    val fp_rx_packets_per_second: Int = -1,
    val fp_rx_bits_per_second: Int = -1,
    val rx_drops_per_second: Int = -1,
    val rx_errors_per_second: Int = -1,
    val tx_packets_per_second: Int = -1,
    val tx_bits_per_second: Int = -1,
    val fp_tx_packets_per_second: Int = -1,
    val fp_tx_bits_per_second: Int = -1,
    val tx_drops_per_second: Int = -1,
    val tx_queue_drops_per_second: Int = -1,
    val tx_errors_per_second: Int = -1
)

data class CommandResponse(
    var status: String?,
    var message: String?
)

data class TokenResponse(
    var token: String
)

data class UpdateRouterResponse(
    var data: NetworkRouter? = null,
    var message: String? = null,
    var errors: List<String>? = null
)

data class User(
    var id: String?,
    var username: String?,
    var created_at: String?,
    var updated_at: String?
)

data class UserLog(
    var id: String?,
    var user: User?,
    var text: String?,
    var created_at: String?,
    var updated_at: String?
)

data class UserLogIndexResponse(
    var message: String?,
    var data: List<UserLog>?,
    var errors: Map<String, List<String>>?
)

interface MikrotikApiService {
    companion object {
        const val API_ROOT_PATH: String = "/api"
    }

    @GET("${API_ROOT_PATH}/user_log")
    fun getUserLogs(): Call<UserLogIndexResponse>

    @GET("${API_ROOT_PATH}/router_traffic/{router}/show")
    fun getRouterTrafficData(
        @Path("router") router_id: Int
    ): Call<RouterTrafficData>

    @FormUrlEncoded
    @PUT("${API_ROOT_PATH}/router/{router}")
    fun updateNetworkRouter(
        @Path("router") routerId: Int,
        @Field("name") name: String,
        @Field("host") host: String,
        @Field("admin_username") admin_username: String,
        @Field("admin_password") password: String
    ): Call<UpdateRouterResponse>

    @GET("${API_ROOT_PATH}/router")
    fun getNetworkRouters(): Call<List<NetworkRouter>>

    @FormUrlEncoded
    @POST("/api/login")
    fun logIn(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<TokenResponse>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @GET("/api/router/{routerId}/interface/index")
    fun getNetworkInterfaces(
        @Path("routerId") routerId: Int,
        @Query("api_token") apiToken: String = ""
    ): Call<List<NetworkInterface>>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @GET("/api/router/{routerId}/log/index")
    fun getLogs(@Path("routerId") routerId: Int): Call<List<RouterLog>>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @POST("/api/router/{routerId}/interface/toggle/{networkInterfaceId}")
    fun toggleNetworkInterface(
        @Path("routerId") routerId: Int,
        @Path("networkInterfaceId") networkInterfaceId: String,
        @Query("api_token") apiToken: String = ""
    ): Call<NetworkInterface>

    @GET("/api/router/{routerId}/wireless/registration_table/index")
    fun getConnectedClients(@Path("routerId") routerId: Int): Call<List<ConnectedClient>>

    @GET("/api/router/{routerId}/wireless/access_list/index")
    fun getAccessList(@Path("routerId") routerId: Int): Call<List<AccessListItem>>

    @POST("/api/router/{routerId}/wireless/access_list/create")
    fun createAccessListItem(@Path("routerId") routerId: Int, @Body accessListItem: AccessListItem): Call<CommandResponse>

    @FormUrlEncoded
    @POST("/api/router/{routerId}/wireless/access_list/delete")
    fun deleteAccessListItem(@Path("routerId") routerId: Int, @Field("id") id: String): Call<CommandResponse>
}
