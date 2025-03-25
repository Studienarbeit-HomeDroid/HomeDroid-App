package com.homedroid.data.login

import android.content.Context
import com.homedroid.data.network.HtmlClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Login @Inject constructor(
    private val htmlClient: HtmlClient,
    private val context: Context
) {

    suspend fun logIn(username: String, password: String): Boolean {
        val token = htmlClient.logIn(username, password)
        return if (token != null) {
            saveUserSession(token)
            true
        } else {
            false
        }
    }

    suspend fun getProtectedDataSync(token: String): Boolean {
        var result = htmlClient.getProtectedData(token)
        if (result == null)
        {
            return false
        }else
        {
            return true
        }

    }


    private fun saveUserSession(token: String) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("token", token).apply()
    }


}