package com.iqbal.app.mikrotikmonitor

import android.content.Context
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment: AppFragment() {
    override fun getLayout() = R.layout.fragment_settings

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?:
        throw Exception("Shared preferences can't be loaded.")

        view.apply {
            edit_text_server_address.apply {
                setText(sharedPref.getString("SERVER_ADDRESS", null))
            }

            button_save_settings.setOnClickListener {
                with (sharedPref.edit()) {
                    putString("SERVER_ADDRESS", edit_text_server_address.text.toString())
                    commit()
                }
            }
        }
    }
}