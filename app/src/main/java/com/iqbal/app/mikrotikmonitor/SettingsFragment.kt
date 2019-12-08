package com.iqbal.app.mikrotikmonitor

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment: AppFragment() {
    override fun getLayout() = R.layout.fragment_settings

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.primary_shared_preference_id),
            Context.MODE_PRIVATE
        ) ?:
        throw Exception("Shared preferences can't be loaded.")

        view.apply {
            edit_text_server_address.apply {
                setText(sharedPref.getString(getString(R.string.server_address_preference_key), null))
            }

            button_save_settings.setOnClickListener {
                with (sharedPref.edit()) {

                    val newServerAddress = edit_text_server_address.text.toString()

                    putString(
                        getString(R.string.server_address_preference_key),
                        newServerAddress
                    )

                    with (commit()) {
                        HttpService.setBaseUrl(newServerAddress)
                        
                        Toast.makeText(context, getString(
                                if (this)
                                    R.string.action_save_settings_success else
                                    R.string.action_save_settings_fail
                            ), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }
}