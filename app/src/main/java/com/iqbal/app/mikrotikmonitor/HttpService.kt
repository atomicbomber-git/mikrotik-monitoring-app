package com.iqbal.app.mikrotikmonitor

import android.content.SharedPreferences
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class HttpService {
    companion object {
        val instance: MikrotikApiService = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8000/")
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
                    .create(MikrotikApiService::class.java)
    }
}