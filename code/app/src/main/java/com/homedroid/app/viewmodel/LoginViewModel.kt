package com.homedroid.app.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homedroid.data.login.Login
import com.homedroid.data.network.HtmlClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val login: Login
): ViewModel(){

    fun logIn(username: String, password: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                Log.i("Login", "Start Login")
                var token = login.logIn(username, password)
                if(token) {
                    Log.i("Login", "Token: $token")
                    callback(true)
                    Log.i("Login", "Login erfolgreich")

                }
                else{
                    callback(false)
                }
            } catch (e: Exception) {
                Log.e("Login", "Fehler beim Login: ${e.message}")
                callback(false)
            }
        }

    }



}