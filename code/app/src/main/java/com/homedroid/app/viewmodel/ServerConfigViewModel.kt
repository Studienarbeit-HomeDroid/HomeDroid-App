package com.homedroid.app.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel

class ServerConfigViewModel : ViewModel() {

    var serverUrl: String? = null
    var certUri: Uri? = null

    fun saveServerConfig(url: String, uri: Uri?) {
        serverUrl = url
        certUri = uri

        Log.d("ServerConfig", "Gespeicherte URL: $serverUrl")
        Log.d("ServerConfig", "Gespeichertes Zertifikat: $certUri")
    }
}