package com.iqbal.app.mikrotikmonitor

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

enum class Feature {
    NETWORK_INTERFACES {
        override fun tabName() = "Network Interfaces"
        override fun fragment() = NetworkInterfacesFragment()
    },

    LOGS {
        override fun tabName() = "Logs"
        override fun fragment() = LogsFragment()
    },

    HOTSPOT {
        override fun tabName() = "Wireless"
        override fun fragment() = SettingsFragment()
    },

    SETTINGS {
        override fun tabName() = "Settings"
        override fun fragment() = SettingsFragment()
    },
    ;

    abstract fun tabName(): String;
    abstract fun fragment(): AppFragment;
}

class MainActivityFragmentAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = Feature.values().size
    override fun createFragment(position: Int) = Feature.values()[position].fragment()
}

abstract class AppFragment(): Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(getLayout(), container, false)

    abstract fun getLayout(): Int
}

