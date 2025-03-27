package com.homedroidv2.app.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.security.KeyStore
import java.time.LocalDateTime
import javax.inject.Inject
import javax.net.ssl.*

@HiltViewModel

class ServerConfigViewModel @Inject constructor(
    private var database: FirebaseDatabase
) : ViewModel() {

    var serverUrl: String? = null
    var certUri: Uri? = null
    var password: String? = null
    private var connected = false
    var html: String? = null

    private val logsRef: DatabaseReference = database.getReference("logs")

    data class LogEntry(val message: String, val timestamp: LocalDateTime = LocalDateTime.now())

    fun logDataToFirestore(logMessage: String) {

        val logEntry = LogEntry(logMessage)
        logsRef.push().setValue(logEntry)

    }


    private lateinit var firebaseAnalytics: FirebaseAnalytics

    fun initAnalytics(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    }

    fun saveServerConfig(context: Context, url: String, uri: Uri?, password: String?, onResult: (Boolean) -> Unit) {
        serverUrl = url
        certUri = uri
        this.password = password

        Log.d("ServerConfig", "Gespeicherte URL: $serverUrl")
        Log.d("ServerConfig", "Gespeichertes Zertifikat: $certUri")

        initAnalytics(context)


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
            FirebaseCrashlytics.getInstance().log("Zertifikat konnte nicht gelesen werden")
            FirebaseCrashlytics.getInstance().recordException(e)
            logAnalyticsError("Zertifikat konnte nicht gelesen werden", e)
            logDataToFirestore("Zertifikat konnte nicht gelesen werden")

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
            FirebaseCrashlytics.getInstance().log("Fehler beim Laden des Zertifikats")
            FirebaseCrashlytics.getInstance().recordException(e)
            logAnalyticsError("Fehler beim Laden des Zertifikats\"", e)
            logDataToFirestore("Fehler beim Laden des Zertifikats")

            throw e
        }

        val kmf = try {
            KeyManagerFactory.getInstance("X509").apply {
                init(keyStore, pwd.toCharArray())
            }
        } catch (e: Exception) {
            Log.e("HTTP", "Fehler beim Initialisieren des KeyManagers: ${e.message}", e)
            showToast(context, "Fehler beim Initialisieren des KeyManagers: ${e.message}")
            FirebaseCrashlytics.getInstance().log("Fehler beim Initialisieren des KeyManagers")
            FirebaseCrashlytics.getInstance().recordException(e)
            logAnalyticsError("Fehler beim Initialisieren des KeyManagers", e)
            logDataToFirestore("Fehler beim Initialisieren des KeyManagers")

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
            FirebaseCrashlytics.getInstance().log("Fehler beim Initialisieren von SSL")
            FirebaseCrashlytics.getInstance().recordException(e)
            logAnalyticsError("Fehler beim Initialisieren von SSL", e)
            logDataToFirestore("Fehler beim Initialisieren von SSL")


            throw e
        }

        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    fun checkConnection(context: Context, onResult:  (Boolean) -> Unit) {
        initAnalytics(context)
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
                if (bodyString != null) {
                    logDataToFirestore(bodyString)
                }
                Log.d("HTTP", "HTML: $html")
                onResult(true)
            } catch (e: Exception) {
                Log.e("HTTP", "Fehler: ${e.message}")
                FirebaseCrashlytics.getInstance().log("Fehler bei der Secure Request")
                FirebaseCrashlytics.getInstance().recordException(e)
                logAnalyticsError("Fehler bei der Secure Request", e)
                logDataToFirestore("Fehler bei der Secure Request")


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
                    if (bodyString != null) {
                        logDataToFirestore(bodyString)
                    }
                    logAnalyticsSuccess("connection_success", response.code, bodyString)
                    onResult(true)

                } catch (e: Exception) {
                    onResult(false)
                    Log.e("DEBUG", "Fehler: ${e.message}", e)
                    logAnalyticsError("connection_error", e)
                    logDataToFirestore("Fehler bei der sendSecureRequest")

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

    private fun logAnalyticsSuccess(eventName: String, responseCode: Int, responseBody: String?) {
        val params = Bundle().apply {
            putString("event_name", eventName)
            putInt("response_code", responseCode)
            putString("response_body", responseBody)
        }
        firebaseAnalytics.logEvent("request_success", params)
    }

    private fun logAnalyticsError(eventName: String, exception: Exception) {
        val params = Bundle().apply {
            putString("event_name", eventName)
            putString("error_message", exception.message)
        }
        firebaseAnalytics.logEvent("request_failure", params)
    }




}