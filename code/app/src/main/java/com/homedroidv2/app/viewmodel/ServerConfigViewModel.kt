package com.homedroidv2.app.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.homedroidv2.data.repositories.DashboardRepository
import com.homedroidv2.data.repositories.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.InputStream
import java.net.URL
import java.security.KeyStore
import java.time.LocalDateTime
import javax.inject.Inject
import javax.net.ssl.*

@HiltViewModel

class ServerConfigViewModel @Inject constructor(
    private var database: FirebaseDatabase,
    private val dashboardRepository: DashboardRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {

    var serverUrl: String? = null
    var certUri: Uri? = null
    var password: String? = null
    private var connected = false
    var html: String? = null

    private val logsRef: DatabaseReference = database.getReference("logs")

    data class LogEntry constructor(val message: String, val timestamp: LocalDateTime = LocalDateTime.now())

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
            logDataToFirestore(" URL: $serverUrl Zertifikat konnte nicht gelesen werden : $e")

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
            logDataToFirestore("URL: $serverUrl  Fehler beim Laden des Zertifikats: $e")

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
            logDataToFirestore("URL: $serverUrl  Fehler beim Initialisieren des KeyManagers : $e")

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
            logAnalyticsError(" URL: $serverUrl Fehler beim Initialisieren von SSL", e)
            logDataToFirestore(" URL: $serverUrl Fehler beim Initialisieren von SSL : $e")


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
                logDataToFirestore(" URL: $serverUrl Fehler bei der Secure Request: $e")


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
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Ein Fehler ist aufgetreten: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                    Log.e("DEBUG", "Fehler: ${e.message}", e)
                    logAnalyticsError("connection_error", e)
                    logDataToFirestore("URL: $serverUrl Fehler bei der sendSecureRequest : $e")

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

    public fun fetchAllDatas(context: Context)
    {
        viewModelScope.launch {
            try {
                fetchHeizungData(context)
                fetchSolarData(context)
            } catch (e: Exception)
            {
                logDataToFirestore("URL: $serverUrl Fehler bei fetchAllDatas : $e")
            }
        }
    }

    fun fetchSolarData(context: Context){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "${serverUrl}/sh/solar/opendtu.php"
                val client = createSecureClient(context)
                Log.d("Solar", "URL: $url")
                val request = Request.Builder().url(url).build()
                Log.d("Solar", "Request: $request")
                val response = client.newCall(request).execute()
                Log.d("Solar", "Response: $response")
                val responseBody =
                    response.body?.string() ?: throw Exception("Response Body ist null")

                val json = JSONObject(responseBody)

                val power = json.getJSONObject("total").getJSONObject("Power")
                val yieldDay = json.getJSONObject("total").getJSONObject("YieldDay")
                val yieldTotal = json.getJSONObject("total").getJSONObject("YieldTotal")

                val leistung = "${power.getDouble("v").toInt()} "
                val tag = "${yieldDay.getDouble("v")}"
                val gesamt = "${yieldTotal.getDouble("v")}"

                dashboardRepository.updateSolarDatas(tag, gesamt)

                Log.d("Solar", "Leistung: $leistung | Tag: $tag | Gesamt: $gesamt")
            } catch (e: Exception) {
                Log.e("Solar", "Fehler beim Abrufen der Solardaten: ${e.message}", e)
                logDataToFirestore("URL: $serverUrl/sh/solar/opendtu.php Fehler bei fetchAllDatas : $e")

                dashboardRepository.updateSolarDatas("err", "err")
            }
        }
    }

    fun fetchHeizungData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "${serverUrl}/sh/visu/heizung/ajax/read_table_vicare_latest.php"
                Log.d("Heizung", "URL: $url")
                val client = createSecureClient(context)
                Log.d("Heizung", "Client: $client")
                val request = Request.Builder().url(url).build()
                Log.d("Heizung", "Request: $request")
                val response = client.newCall(request).execute()
                Log.d("Heizung", "Response: $response")
                val responseBody = response.body?.string()
                val data =
                    JSONObject("{\"array\":$responseBody}").getJSONArray("array").getJSONObject(0)

                val tempAussen = data.getString("Temp_Aussen")
                val timestamp = data.getString("timestamp")
                val modus = data.getString("Modus")
                val brennerStunden = data.getString("Brenner_Betriebsstunden")
                val brennerStarts = data.getString("Brenner_Starts")

                dashboardRepository.updateHeizungValue("1", tempAussen)
                dashboardRepository.updateHeizungValue("2", modus)
                dashboardRepository.updateHeizungValue("3", brennerStunden)
                dashboardRepository.updateHeizungValue("4", brennerStarts)
                dashboardRepository.updateHeizungValue("5", timestamp)

                println("Außentemperatur: $tempAussen °C")
                println("Zeitstempel: $timestamp")
                println("Modus: $modus")
                println("Brennerstunden: $brennerStunden")
                println("Brennerstarts: $brennerStarts")
            }
            catch (e:Exception)
            {
                Log.e("Heizung", "Fehler beim Abrufen der Solardaten: ${e.message}", e)
                logDataToFirestore("URL: $serverUrl/sh/solar/opendtu.php Fehler bei fetchAllDatas : $e")
                dashboardRepository.updateHeizungValue("1", "err")
                dashboardRepository.updateHeizungValue("2", "err")
                dashboardRepository.updateHeizungValue("3", "err")
                dashboardRepository.updateHeizungValue("4", "err")
                dashboardRepository.updateHeizungValue("5", "err")
            }
        }
    }





    private fun  getDeviceDatas(context: Context, onResult: (Boolean) -> Unit)
    {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val client = createSecureClient(context)
                val url = serverUrl ?: return@launch

                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()


                onResult(true)

            } catch (e: Exception) {
                onResult(false)

                logDataToFirestore("URL: $serverUrl Fehler bei der sendSecureRequest : $e")

            }
        }

    }








}