package com.atomicbomber.exampleproject.mikrotikmonitor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class MainActivity : FragmentActivity() {
    val primaryRouterId: Int = 1
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

//        val tabLayout = view.findViewById(R.id.tab_layout)
//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            tab.text = "OBJECT ${(position + 1)}"
//        }.attach()

        TabLayoutMediator(main_activity_tab_layout, main_activity_view_pager) { tab, position ->
            tab.text = Feature.values()[position].tabName()
        }.attach()
    }

//    private fun setUpInterfaceRecyclerView() {
//        interfaceIndexRecyclerView.layoutManager = LinearLayoutManager(this.applicationContext)
//        interfaceIndexRecyclerView.adapter = NetworkInterfaceListAdapter(this.networkInterfaceList)
//    }
//
//    private fun loadData() {
//        service.getNetworkInterfaces(Config.PRIMARY_ROUTER_ID)
//            .enqueue(object : Callback<List<NetworkInterface>> {
//                override fun onFailure(call: Call<List<NetworkInterface>>, t: Throwable) {
//                }
//
//                override fun onResponse(
//                    call: Call<List<NetworkInterface>>,
//                    response: Response<List<NetworkInterface>>
//                ) {
//                    response.body()?.apply {
//                        networkInterfaceList
//                            .addAll(this as ArrayList<NetworkInterface>)
//                        interfaceIndexRecyclerView.adapter?.notifyDataSetChanged()
//                    }
//                }
//            })
//    }
//
//    inner class NetworkInterfaceListAdapter(private val dataset: List<NetworkInterface>) :
//        RecyclerView.Adapter<NetworkInterfaceListAdapter.ItemViewHolder>() {
//
//        inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view)
//
//        override fun onCreateViewHolder(parent: ViewGroup,
//                                        viewType: Int): ItemViewHolder {
//            val view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.interface_item, parent, false)
//
//            return ItemViewHolder(view)
//        }
//
//        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
//
//            val networkInterface = this.dataset[position]
//
//            holder.itemView.apply {
//                interface_id.text = networkInterface.id
//                name.text = networkInterface.name
//
//                disabled.apply {
//                    text = if (networkInterface.disabled == "false") "False" else "True"
//                    setTextColor(
//                        if (networkInterface.disabled == "true")
//                            resources.getColor(R.color.colorPrimary)
//                        else
//                            resources.getColor(R.color.colorAccent)
//                    )
//                }
//                button_toggle_disabled.apply {
//                    text = if (networkInterface.disabled == "false") "Disable" else "Enable" }
//            }
//
//            holder.itemView.button_toggle_disabled.setOnClickListener {
//                toggleNetworkInterface(networkInterface.id)
//            }
//        }
//
//        private fun toggleNetworkInterface(networkInterfaceId: String) {
//            this@MainActivity.service.toggleNetworkInterface(
//                this@MainActivity.primaryRouterId,
//                networkInterfaceId
//            )
//                .enqueue(object: Callback<String> {
//                    override fun onFailure(call: Call<String>, t: Throwable) {
//                    }
//
//                    override fun onResponse(call: Call<String>, response: Response<String>) {
//                        loadData()
//                    }
//                })
//        }
//
//        override fun getItemCount() = dataset.size
//    }
}


