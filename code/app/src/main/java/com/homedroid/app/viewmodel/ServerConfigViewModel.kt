package com.homedroid.app.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.security.KeyStore
import javax.inject.Inject
import javax.net.ssl.*

@HiltViewModel

class ServerConfigViewModel @Inject constructor() : ViewModel() {

    var serverUrl: String? = null
    var certUri: Uri? = null
    var password: String? = null
    private var connected = false
    var html: String? = null


    fun saveServerConfig(context: Context, url: String, uri: Uri?, password: String?, onResult: (Boolean) -> Unit) {
        serverUrl = url
        certUri = uri
        this.password = password

        Log.d("ServerConfig", "Gespeicherte URL: $serverUrl")
        Log.d("ServerConfig", "Gespeichertes Zertifikat: $certUri")

        sendSecureRequest(context) { result ->
            if (result)
            {
                onResult(true)
            }
            else
            {
                onResult(false)
            }
        }
    }

    fun setConfig( url: String, uri: Uri?, pwd: String) {
        Log.i("Cert", "set Config")
        Log.i("Cert", "Server URL: $url")
        Log.i("Cert", "Cert URI: $uri")
        Log.i("Cert", "CERT PWD: $pwd")
        serverUrl = url
        certUri = uri
        password = pwd
    }



    private fun getCertificateStream(context: Context): InputStream? {
        return certUri?.let { context.contentResolver.openInputStream(it) }
    }

    private fun createSecureClient(context: Context): OkHttpClient {
        val certStream = try {
            getCertificateStream(context) ?: throw Exception("Kein Zertifikat gefunden")
        } catch (e: Exception) {
            Log.e("HTTP", "Zertifikat konnte nicht gelesen werden ${e.message}", e)
            showToast(context, "Zertifikat konnte nicht gelesen werden: ${e.message}")
            throw e
        }

        val pwd = password ?: ""

        val keyStore = try {
            KeyStore.getInstance("PKCS12").apply {
                load(certStream, pwd.toCharArray())
            }
        } catch (e: Exception) {
            Log.e("HTTP", "Fehler beim Laden des Zertifikats: ${e.message}", e)
            showToast(context, "Fehler beim Laden des Zertifikats: ${e.message}")
            throw e
        }

        val kmf = try {
            KeyManagerFactory.getInstance("X509").apply {
                init(keyStore, pwd.toCharArray())
            }
        } catch (e: Exception) {
            Log.e("HTTP", "Fehler beim Initialisieren des KeyManagers: ${e.message}", e)
            showToast(context, "Fehler beim Initialisieren des KeyManagers: ${e.message}")
            throw e
        }

        val trustAllManager = object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
            override fun checkClientTrusted(
                chain: Array<java.security.cert.X509Certificate>,
                authType: String
            ) {
            }

            override fun checkServerTrusted(
                chain: Array<java.security.cert.X509Certificate>,
                authType: String
            ) {
            }
        }

        val sslContext = try {
            SSLContext.getInstance("TLS").apply {
                init(kmf.keyManagers, arrayOf(trustAllManager), null)
            }
        } catch (e: Exception) {
            Log.e("HTTP", "Fehler beim Initialisieren von SSL: ${e.message}", e)
            showToast(context, "Fehler beim Initialisieren von SSL: ${e.message}")
            throw e
        }

        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    fun checkConnection(context: Context, onResult:  (Boolean) -> Unit) {
        Log.d("Result", "Checkconnection")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val client = createSecureClient(context)
                val url = serverUrl ?: return@launch onResult(false)

                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                Log.d("HTTP", "Code: ${response.code}")
                Log.d("HTTP", "After")
                val bodyString = response.body?.string()
                html = bodyString
                Log.d("HTTP", "HTML: $html")
                onResult(true)
            } catch (e: Exception) {
                Log.e("HTTP", "Fehler: ${e.message}")
                onResult(false)
            }
        }
    }

        fun sendSecureRequest(context: Context, onResult: (Boolean) -> Unit) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val client = createSecureClient(context)
                    val url = serverUrl ?: return@launch

                    val request = Request.Builder().url(url).build()
                    val response = client.newCall(request).execute()

                    Log.d("DEBUG", "Code: ${response.code}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Anfrage war erfolgreich", Toast.LENGTH_LONG).show()
                    }

                    val prefs = context.getSharedPreferences("cert_prefs", Context.MODE_PRIVATE)
                    Log.d("DEBUG", "SharedPreferences geladen")

                    prefs.edit()
                        .putString("server_url", url)
                        .putString("cert_uri", certUri?.toString())
                        .putString("cert_pwd", password)
                        .apply()
                    Log.d("DEBUG", "Preferences gespeichert")
                    val bodyString = response.body?.string()
                    html = bodyString
                    onResult(true)

                } catch (e: Exception) {
                    onResult(false)
                    Log.e("DEBUG", "Fehler: ${e.message}", e)
                }
            }
        }

        fun getServerConfig(context: Context): Map<String, String> {
            val prefs = context.getSharedPreferences("cert_prefs", Context.MODE_PRIVATE)
            return mapOf(
                "server_url" to prefs.getString("server_url", "").orEmpty(),
                "cert_uri" to prefs.getString("cert_uri", "").orEmpty(),
                "cert_pwd" to prefs.getString("cert_pwd", "").orEmpty()
            )
        }

        private fun showToast(context: Context, message: String) {
            viewModelScope.launch(Dispatchers.Main) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }

}