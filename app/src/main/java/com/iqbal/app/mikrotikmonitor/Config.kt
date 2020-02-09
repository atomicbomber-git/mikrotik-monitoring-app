package com.iqbal.app.mikrotikmonitor

class Config {
    companion object {
        const val PRIMARY_ROUTER_ID: Int = 1
        const val SHARED_PREF_PRIMARY_ID = "PRIMARY"
        const val SHARED_PREF_PRIMARY_KEY_API_TOKEN = "API_TOKEN"
        const val SHARED_PREF_PRIMARY_KEY_SERVER_HOST = "SERVER_HOST"
        const val SERVER_DEFAULT_HOST = "http://localhost:8000"
    }
}