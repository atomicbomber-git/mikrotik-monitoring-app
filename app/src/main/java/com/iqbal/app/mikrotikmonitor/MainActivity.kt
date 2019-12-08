package com.iqbal.app.mikrotikmonitor

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val demoCollectionAdapter = MainActivityFragmentAdapter(this)
        main_activity_view_pager.adapter = demoCollectionAdapter

        TabLayoutMediator(main_activity_tab_layout, main_activity_view_pager) { tab, position ->
            tab.text = Feature.values()[position].tabName()
        }.attach()
    }
}


