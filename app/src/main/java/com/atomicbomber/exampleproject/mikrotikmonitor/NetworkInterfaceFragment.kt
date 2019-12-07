package com.atomicbomber.exampleproject.mikrotikmonitor

import android.app.Activity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_log.*

enum class Feature {
    NETWORK_INTERFACES {
        override fun tabName() = "Network Interfaces"
    },

    LOGS {
        override fun tabName() = "Logs"
    },

    SETTINGS {
      override fun tabName() = "Settings"
    };

    abstract fun tabName(): String;
}

class MainActivityFragmentAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    public lateinit var fragment: Fragment

    override fun getItemCount(): Int = Feature.values().size

    override fun createFragment(position: Int): Fragment {
        this.fragment = NetworkInterfaceFragment().apply {
            arguments = Bundle().apply {
                putString("NAME", Feature.values()[position].tabName())
            }
        }
        return this.fragment
    }
}

class NetworkInterfaceFragment(): Fragment() {
    lateinit var feature: Feature

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // val tabName: String? = savedInstanceState?.getString("NAME", null)
        return inflater.inflate(R.layout.fragment_network_interface, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}