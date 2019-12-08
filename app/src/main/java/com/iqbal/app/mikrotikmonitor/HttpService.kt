package com.iqbal.app.mikrotikmonitor

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class HttpService {
    companion object {
        var instance: MikrotikApiService

        private var sharedPreferences: SharedPreferences

        init {
            Common.appContext.let {
                sharedPreferences = it.getSharedPreferences(
                    it.getString(R.string.primary_shared_preference_id),
                    Context.MODE_PRIVATE
                )

                instance = createService(
                    sharedPreferences.getString(
                        it.getString(R.string.server_address_preference_key),
                        it.getString(R.string.server_address_preference_default_val)
                    ) ?: it.getString(R.string.server_address_preference_default_val)
                )
            }
        }

        private fun createRetrofit(baseUrl: String): Retrofit {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
        }

        private fun createService(baseUrl: String): MikrotikApiService {
            return createRetrofit(baseUrl).create(MikrotikApiService::class.java)
        }

        fun setBaseUrl(baseUrl: String) {
            instance = createService(baseUrl)
        }
    }
}