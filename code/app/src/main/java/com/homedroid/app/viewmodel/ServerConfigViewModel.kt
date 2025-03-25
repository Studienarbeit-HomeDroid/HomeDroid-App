package com.homedroid.app.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.security.KeyStore
import javax.net.ssl.*

class ServerConfigViewModel : ViewModel() {

    var serverUrl: String? = null
    var certUri: Uri? = null
    var password: String? = null

    fun saveServerConfig(context: Context, url: String, uri: Uri?) {
        serverUrl = url
        certUri = uri

        Log.d("ServerConfig", "Gespeicherte URL: $serverUrl")
        Log.d("ServerConfig", "Gespeichertes Zertifikat: $certUri")

        sendSecureRequest(context)
    }

    private fun getCertificateStream(context: Context): InputStream? {
        return certUri?.let { context.contentResolver.openInputStream(it) }
    }

    private fun createSecureClient(context: Context): OkHttpClient {
        val certStream = getCertificateStream(context) ?: throw Exception("Kein Zertifikat gefunden")
        val pwd = password ?: ""

        // Client-Zertifikat laden
        val keyStore = KeyStore.getInstance("PKCS12")
        keyStore.load(certStream, pwd.toCharArray())

        val kmf = KeyManagerFactory.getInstance("X509")
        kmf.init(keyStore, pwd.toCharArray())

        // Trust Manager für Server-Zertifikat (z. B. self-signed akzeptieren)
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)
        val trustManager = trustManagerFactory.trustManagers
            .filterIsInstance<X509TrustManager>()
            .first()

        // SSL-Kontext mit beiden
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(kmf.keyManagers, arrayOf(trustManager), null)

        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .hostnameVerifier { _, _ -> true } // ⚠️ Nur für Testzwecke!
            .build()
    }

    fun sendSecureRequest(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val client = createSecureClient(context)
                val url = serverUrl ?: return@launch

                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                Log.d("HTTP", "Code: ${response.code}")
                Log.d("HTTP", "Body: ${response.body?.string()}")
            } catch (e: Exception) {
                Log.e("HTTP", "Fehler: ${e.message}", e)
            }
        }
    }
}