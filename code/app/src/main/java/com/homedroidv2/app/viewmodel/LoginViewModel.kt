package com.homedroidv2.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homedroidv2.data.login.Login
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [VERALTET] – Diese LoginViewModel-Klasse wird aktuell nicht mehr verwendet.
 *
 * Sie wurde durch eine neue Implementierung ersetzt und bleibt lediglich aus dokumentarischen
 * Gründen im Projekt bestehen. Die Funktionalität zur Benutzeranmeldung über die
 * Login-Komponente ist inzwischen anderweitig gelöst.
 */

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