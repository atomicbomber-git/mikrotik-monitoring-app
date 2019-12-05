package com.atomicbomber.exampleproject.mikrotikmonitor

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.interface_item.view.*
import retrofit2.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.moshi.MoshiConverterFactory


class MainActivity : AppCompatActivity() {

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

        setUpInterfaceRecyclerView()
        loadData()
    }

    private fun setUpInterfaceRecyclerView() {
        interfaceIndexRecyclerView.layoutManager = LinearLayoutManager(this.applicationContext)
        interfaceIndexRecyclerView.adapter = NetworkInterfaceListAdapter(this.networkInterfaceList)
    }

    private fun loadData() {
        service.getNetworkInterfaces(1)
            .enqueue(object : Callback<List<NetworkInterface>> {
                override fun onFailure(call: Call<List<NetworkInterface>>, t: Throwable) {
                    Log.e("ERROR", t.message.toString() + "XXXX")
                }

                override fun onResponse(
                    call: Call<List<NetworkInterface>>,
                    response: Response<List<NetworkInterface>>
                ) {
                    response.body()?.apply {
//                        this.forEach { Log.d("TESTING", it.name) }


                        networkInterfaceList
                            .addAll(this as ArrayList<NetworkInterface>)

                        interfaceIndexRecyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            })
    }
}

class NetworkInterfaceListAdapter(private val dataset: List<NetworkInterface>) :
    RecyclerView.Adapter<NetworkInterfaceListAdapter.ItemViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ItemViewHolder {
        // create a new view
        // set the view's size, margins, paddings and layout parameters

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.interface_item, parent, false)

        return ItemViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val networkInterface = this.dataset[position]

        holder.itemView.apply {
            interface_id.text = networkInterface.id
            name.text = networkInterface.name


            disabled.apply {
                text = if (networkInterface.disabled == "false") "False" else "True"
                setTextColor(
                    if (networkInterface.disabled == "true")
                        resources.getColor(R.color.colorPrimary)
                    else
                        resources.getColor(R.color.colorAccent)
                )
            }

            button_toggle_disabled.text = if (networkInterface.disabled == "false") "Disable" else "Enable"
//            button_toggle_disabled.setBackgroundColor(resources.getColor(R.color.colorAccent))
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataset.size
}
