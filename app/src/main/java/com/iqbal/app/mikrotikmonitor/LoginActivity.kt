package com.iqbal.app.mikrotikmonitor

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViews()
        setDefaultServerHost()
    }

    private fun setUpViews() {
        setContentView(R.layout.activity_login)
        login.setOnClickListener {
            try {
                HttpService.setBaseUrl(server_host.text.toString())
            } catch (e: Exception) {
                return@setOnClickListener
            }

            getTokenFromServer(
                username = this.username.text.toString(),
                password = this.password.text.toString(),
                onSuccess = { token ->
                    Common.getPrimarySharedPreferences()
                        .edit()?.let {
                            it.putString(Config.SHARED_PREF_PRIMARY_KEY_API_TOKEN, token.token)
                            it.commit()
                        }

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                },
                onFailure = {
                    Toast.makeText(
                        Common.appContext,
                        getString(R.string.invalid_credentials),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    private fun setDefaultServerHost() {
        val inputServerHost = server_host.text.toString()

        if (inputServerHost.equals("")) {
            val currentServerHost = Common.getCurrentServerHost()

            if (currentServerHost !== null) {
                server_host.setText(currentServerHost)
            } else {
                server_host.setText(Config.SERVER_DEFAULT_HOST)
            }
        }
    }

    private fun getTokenFromServer(
        username: String,
        password: String,
        onSuccess: (TokenResponse) -> Unit,
        onFailure: () -> Unit
    ): Unit {
        HttpService.instance.logIn(username, password)
            .enqueue(object : retrofit2.Callback<TokenResponse> {
                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    onFailure()
                }

                override fun onResponse(
                    call: Call<TokenResponse>,
                    response: Response<TokenResponse>
                ) {
                    if (!response.isSuccessful) {
                        onFailure()
                        return
                    }

                    response.body().let { token ->
                        if (token !== null) {
                            onSuccess(token)
                        } else {
                            onFailure()
                        }
                    }
                }
            })
    }
}
