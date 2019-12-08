package com.iqbal.app.mikrotikmonitor

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class MainActivity : FragmentActivity() {
    val primaryRouterId: Int = Config.PRIMARY_ROUTER_ID
    lateinit var service: MikrotikApiService
    lateinit var networkInterfaceList: ArrayList<NetworkInterface>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        service = retrofit.create(MikrotikApiService::class.java)
        networkInterfaceList = ArrayList()

        val demoCollectionAdapter = MainActivityFragmentAdapter(this)
        main_activity_view_pager.adapter = demoCollectionAdapter

        TabLayoutMediator(main_activity_tab_layout, main_activity_view_pager) { tab, position ->
            tab.text = Feature.values()[position].tabName()
        }.attach()
    }
}


