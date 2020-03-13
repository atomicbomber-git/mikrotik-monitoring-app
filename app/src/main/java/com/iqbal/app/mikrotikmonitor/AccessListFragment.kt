package com.iqbal.app.mikrotikmonitor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.access_list_item.view.*
import kotlinx.android.synthetic.main.fragment_access_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface accessListItemDeleteListener {
    fun onAccessListItemDelete()
}

class AccessListFragment : AppFragment(), accessListItemDeleteListener, CredentialProvider {
    private val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
    private val accessList: ArrayList<AccessListItem> = ArrayList()
    private val adapter: AccessListFragment.AccessListAdapter =
        AccessListFragment.AccessListAdapter(accessList, this, this)
    lateinit var apiToken: String


    override fun onAccessListItemDelete() {
        loadData()
    }

    override fun getToken(): String {
        return this.apiToken
    }

    override fun getLayout(): Int {
        return R.layout.fragment_access_list
    }

    private fun goToLoginActivity() {
        startActivity(Intent(Common.appContext, LoginActivity::class.java))
        activity?.finish()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Common.getApiToken().let { apiToken ->
            if (apiToken === null) {
                goToLoginActivity()
                return
            }

            this.apiToken = apiToken
        }

        swipe_refresh_layout.setOnRefreshListener {
            loadData()
        }

        setUpRecyclerView()
        loadData()
    }

    private fun setUpRecyclerView() {
        recycler_view.layoutManager = this.layoutManager
        recycler_view.adapter = adapter
    }

    private fun loadDataFinished() {
        swipe_refresh_layout?.apply {
            isRefreshing = false
        }
    }

    private fun loadData() {
        swipe_refresh_layout?.apply {
            isRefreshing = true
        }

        HttpService.instance.getAccessList(Config.PRIMARY_ROUTER_ID, this.apiToken)
            .enqueue(object : Callback<List<AccessListItem>> {
                override fun onFailure(call: Call<List<AccessListItem>>, t: Throwable) {
                    loadDataFinished()
                    fail(t.message)
                }

                override fun onResponse(
                    call: Call<List<AccessListItem>>,
                    response: Response<List<AccessListItem>>
                ) {
                    loadDataFinished()

                    if (response.code() != 200) {
                        fail(response.message())
                    }

                    response.body()?.also { responseAccessList ->
                        accessList.clear()
                        accessList.addAll(responseAccessList)
                        adapter.notifyDataSetChanged()
                    }
                }

                private fun fail(message: String? = "Failed to load data.") {
                    Toast.makeText(context, message, Toast.LENGTH_LONG)
                        .show()
                }
            })
    }

    class AccessListAdapter(
        private val accessList: ArrayList<AccessListItem>,
        private val accessListItemDeleteListener: accessListItemDeleteListener,
        private val credentialProvider: CredentialProvider
    ) :
        RecyclerView.Adapter<AccessListAdapter.ViewHolder>() {

        class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.access_list_item,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int = this.accessList.size

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.view.also { view ->
                accessList[position].also { accessListItem ->
                    view.mac_address.text = accessListItem.mac_address

                    view.deleteAccessListItemButton.setOnClickListener {

                        HttpService.instance.deleteAccessListItem(
                            Config.PRIMARY_ROUTER_ID,
                            accessListItem.id,
                            credentialProvider.getToken()
                        )
                            .enqueue(object : Callback<CommandResponse> {
                                override fun onFailure(call: Call<CommandResponse>, t: Throwable) {
                                    fail(t.message)
                                }

                                override fun onResponse(
                                    call: Call<CommandResponse>,
                                    response: Response<CommandResponse>
                                ) {
                                    if (response.code() != 200) {
                                        fail(response.message())
                                        return
                                    }

                                    response.body()?.also { commandResponse ->
                                        Toast.makeText(
                                            Common.appContext,
                                            commandResponse.status,
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }

                                    accessListItemDeleteListener.onAccessListItemDelete()
                                }

                                private fun fail(message: String? = Common.appContext.getString(R.string.ACTION_FAILED)) {
                                    Toast.makeText(Common.appContext, message, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            })

                    }
                }
            }
        }
    }
}