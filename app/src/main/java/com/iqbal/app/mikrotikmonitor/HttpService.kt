package com.iqbal.app.mikrotikmonitor

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.itkacher.okhttpprofiler.OkHttpProfilerInterceptor
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class HttpService {
    companion object {
        var instance: MikrotikApiService

        private var sharedPreferences: SharedPreferences

        init {
            Common.appContext.let {
                sharedPreferences = Common.getPrimarySharedPreferences()

                try {
                    instance = createService(
                        sharedPreferences.getString(
                            Config.SHARED_PREF_PRIMARY_KEY_SERVER_HOST,
                            Config.SERVER_DEFAULT_HOST
                        ) ?: Config.SERVER_DEFAULT_HOST
                    )
                } catch (e: Exception) {
                    instance = createService(Config.SERVER_DEFAULT_HOST)
                }
            }
        }

        private fun createRetrofit(baseUrl: String): Retrofit {
            return Retrofit.Builder()
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor(OkHttpProfilerInterceptor())
                        .build()
                )
                .baseUrl(baseUrl)
                .addConverterFactory(
                    MoshiConverterFactory.create()
                )
                .build()
        }

        private fun createService(baseUrl: String): MikrotikApiService {
            return createRetrofit(baseUrl).create(MikrotikApiService::class.java)
        }

        fun setBaseUrl(baseUrl: String) {
            with(Common.getPrimarySharedPreferences().edit()) {
                putString(Config.SHARED_PREF_PRIMARY_KEY_SERVER_HOST, baseUrl)
                commit()
            }

            instance = createService(baseUrl)
        }
    }
}