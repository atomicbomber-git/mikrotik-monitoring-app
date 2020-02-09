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
            Config.SHARED_PREF_PRIMARY_ID,
            Context.MODE_PRIVATE
        ) ?:
        throw Exception("Shared preferences can't be loaded.")

        view.apply {
            val currentServerAddress: String? = sharedPref.getString(Config.SHARED_PREF_PRIMARY_KEY_SERVER_HOST, null)
            edit_text_server_address.setText(currentServerAddress)

            button_save_settings.setOnClickListener {
                val newServerAddress = edit_text_server_address.text.toString()

                try {
                    HttpService.setBaseUrl(newServerAddress)
                }
                catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT)
                        .show()
                }
                finally {
                    Toast.makeText(context, getString(R.string.action_save_settings_success), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}