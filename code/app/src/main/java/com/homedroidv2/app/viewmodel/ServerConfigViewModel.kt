package com.homedroidv2.app.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.security.KeyChain
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.homedroidv2.data.model.ParsedDevices
import com.homedroidv2.data.model.ParsedGroup
import com.homedroidv2.data.repositories.DashboardRepository
import com.homedroidv2.data.repositories.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.net.Socket
import java.net.URL
import java.net.URLEncoder
import java.security.KeyStore
import java.security.PrivateKey
import java.time.LocalDateTime
import javax.inject.Inject
import javax.net.ssl.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import okhttp3.FormBody

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
    var requestCount = 0
    var deviceCount = 0

    private val _certAlias = mutableStateOf<String?>(null)
    var certAlias: State<String?> = _certAlias

    fun setCertAlias(alias: String?) {
        _certAlias.value = alias
    }

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

        sendSecureRequest( context, true) { result ->
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

    fun checkConnection(context: Context, url: String, alias: String?, onResult: (Boolean) -> Unit) {
        Log.d("DEBUG MAIN", "ALIAS 3: $alias")

        serverUrl = url
        setCertAlias(alias)
        Log.d("ServerConfig", "Gespeicherte URL: $serverUrl")
        Log.d("ServerConfig", "Gespeichertes Zertifikat: $alias")
        initAnalytics(context)

        sendSecureRequest(context, false) { result ->
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

    fun setConfig( url: String, uri: Uri?, pwd: String, alias: State<String?> ) {
        Log.i("Cert", "set Config")
        Log.i("Cert", "Server URL: $url")
        Log.i("Cert", "Cert URI: $uri")
        Log.i("Cert", "CERT PWD: $pwd")
        serverUrl = url
        certUri = uri
        password = pwd
        certAlias = alias
    }

    private fun getCertificateStream(context: Context): InputStream? {
        return certUri?.let { context.contentResolver.openInputStream(it) }
    }

    private fun createSecureClient(context: Context): OkHttpClient {
        val alias = try {
            certAlias.value ?: throw Exception("Kein Zertifikatsalias gesetzt")
        } catch (e: Exception) {
            logDataToFirestore("URL: $serverUrl Fehler: Kein Zertifikatsalias gesetzt: $e")
            throw e
        }

        val privateKey = try {
            KeyChain.getPrivateKey(context, alias)
        } catch (e: Exception) {
            logDataToFirestore("URL: $serverUrl Fehler beim Abrufen des PrivateKeys von KeyChain: $e")
            throw e
        }

        val certChain = try {
            KeyChain.getCertificateChain(context, alias)
        } catch (e: Exception) {
            logDataToFirestore("URL: $serverUrl Fehler beim Abrufen der Zertifikatskette von KeyChain: $e")
            throw e
        }

        val keyManager = try {
            object : X509KeyManager {
                override fun getClientAliases(keyType: String?, issuers: Array<java.security.Principal>?): Array<String>? {
                    return arrayOf(alias)
                }

                override fun chooseClientAlias(keyTypes: Array<String>?, issuers: Array<java.security.Principal>?, socket: Socket?): String {
                    return alias
                }

                override fun getCertificateChain(alias: String?): Array<java.security.cert.X509Certificate> {
                    return certChain
                        ?.mapNotNull { it as? java.security.cert.X509Certificate }
                        ?.toTypedArray()
                        ?: emptyArray()
                }

                override fun getPrivateKey(alias: String?): PrivateKey? {
                    return privateKey
                }

                override fun getServerAliases(keyType: String?, issuers: Array<java.security.Principal>?): Array<String>? = null
                override fun chooseServerAlias(keyType: String?, issuers: Array<java.security.Principal>?, socket: Socket?): String? = null
            }
        } catch (e: Exception) {
            logDataToFirestore("URL: $serverUrl Fehler beim Erstellen des KeyManagers: $e")
            throw e
        }

        val trustAllManager = object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
        }

        val sslContext = try {
            SSLContext.getInstance("TLS").apply {
                init(arrayOf(keyManager), arrayOf(trustAllManager), null)
            }
        } catch (e: Exception) {
            logDataToFirestore("URL: $serverUrl Fehler beim Initialisieren von SSLContext: $e")
            throw e
        }

        return try {
            OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustAllManager)
                .hostnameVerifier { _, _ -> true }
                .build()
        } catch (e: Exception) {
            logDataToFirestore("URL: $serverUrl Fehler beim Erstellen des OkHttpClient: $e")
            throw e
        }
    }

//    private fun createSecureClient(context: Context): OkHttpClient {
//        val alias = certAlias.value ?: throw Exception("Kein Zertifikatsalias gesetzt")
//
//        val privateKey = KeyChain.getPrivateKey(context, alias)
//        val certChain = KeyChain.getCertificateChain(context, alias)
//
//        val keyManager = object : X509KeyManager {
//            override fun getClientAliases(keyType: String?, issuers: Array<java.security.Principal>?): Array<String>? {
//                return arrayOf(alias)
//            }
//
//            override fun chooseClientAlias(keyTypes: Array<String>?, issuers: Array<java.security.Principal>?, socket: Socket?): String {
//                return alias
//            }
//
//            override fun getCertificateChain(alias: String?): Array<java.security.cert.X509Certificate> {
//                return certChain
//                    ?.mapNotNull { it as? java.security.cert.X509Certificate }
//                    ?.toTypedArray()
//                    ?: emptyArray()
//            }
//
//            override fun getPrivateKey(alias: String?): PrivateKey? {
//                return privateKey
//            }
//
//            override fun getServerAliases(keyType: String?, issuers: Array<java.security.Principal>?): Array<String>? = null
//            override fun chooseServerAlias(keyType: String?, issuers: Array<java.security.Principal>?, socket: Socket?): String? = null
//        }
//
//        val trustAllManager = object : X509TrustManager {
//            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
//            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
//            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
//        }
//
//        val sslContext = SSLContext.getInstance("TLS")
//        sslContext.init(arrayOf(keyManager), arrayOf(trustAllManager), null)
//
//        return OkHttpClient.Builder()
//            .sslSocketFactory(sslContext.socketFactory, trustAllManager)
//            .hostnameVerifier { _, _ -> true }
//            .build()
//    }

//    private fun createSecureClient(context: Context): OkHttpClient {
////        val certStream = try {
////            getCertificateStream(context) ?: throw Exception("Kein Zertifikat gefunden")
////        } catch (e: Exception) {
////            Log.e("HTTP", "Zertifikat konnte nicht gelesen werden ${e.message}", e)
////            showToast(context, "Zertifikat konnte nicht gelesen werden: ${e.message}")
////            FirebaseCrashlytics.getInstance().log("Zertifikat konnte nicht gelesen werden")
////            FirebaseCrashlytics.getInstance().recordException(e)
////            logAnalyticsError("Zertifikat konnte nicht gelesen werden", e)
////            logDataToFirestore(" URL: $serverUrl Zertifikat konnte nicht gelesen werden : $e")
////
////            throw e
////        }
////
////        val pwd = password ?: ""
////
////        val keyStore = try {
////            KeyStore.getInstance("PKCS12").apply {
////                load(certStream, pwd.toCharArray())
////            }
////        } catch (e: Exception) {
////            Log.e("HTTP", "Fehler beim Laden des Zertifikats: ${e.message}", e)
////            showToast(context, "Fehler beim Laden des Zertifikats: ${e.message}")
////            FirebaseCrashlytics.getInstance().log("Fehler beim Laden des Zertifikats")
////            FirebaseCrashlytics.getInstance().recordException(e)
////            logAnalyticsError("Fehler beim Laden des Zertifikats\"", e)
////            logDataToFirestore("URL: $serverUrl  Fehler beim Laden des Zertifikats: $e")
////
////            throw e
////        }
//        val alias = certAlias.value
//            ?: throw Exception("Kein Zertifikatsalias gesetzt")
//
//        val privateKey = KeyChain.getPrivateKey(context, alias)
//        val certChain = KeyChain.getCertificateChain(context, alias)
//
//        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
//            load(null)
//            setKeyEntry(
//                alias,
//                privateKey,
//                null,
//                certChain
//            )
//        }
//
//        val kmf = try {
//            KeyManagerFactory.getInstance("X509").apply {
//                init(keyStore, null)
//            }
//        } catch (e: Exception) {
//            Log.e("HTTP", "Fehler beim Initialisieren des KeyManagers: ${e.message}", e)
//            showToast(context, "Fehler beim Initialisieren des KeyManagers: ${e.message}")
//            FirebaseCrashlytics.getInstance().log("Fehler beim Initialisieren des KeyManagers")
//            FirebaseCrashlytics.getInstance().recordException(e)
//            logAnalyticsError("Fehler beim Initialisieren des KeyManagers", e)
//            logDataToFirestore("URL: $serverUrl  Fehler beim Initialisieren des KeyManagers : $e")
//
//            throw e
//        }
//
//        val trustAllManager = object : X509TrustManager {
//            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
//            override fun checkClientTrusted(
//                chain: Array<java.security.cert.X509Certificate>,
//                authType: String
//            ) {
//            }
//
//            override fun checkServerTrusted(
//                chain: Array<java.security.cert.X509Certificate>,
//                authType: String
//            ) {
//            }
//        }
//
//        val sslContext = try {
//            SSLContext.getInstance("TLS").apply {
//                init(kmf.keyManagers, arrayOf(trustAllManager), null)
//            }
//        } catch (e: Exception) {
//            Log.e("HTTP", "Fehler beim Initialisieren von SSL: ${e.message}", e)
//            showToast(context, "Fehler beim Initialisieren von SSL: ${e.message}")
//            FirebaseCrashlytics.getInstance().log("Fehler beim Initialisieren von SSL")
//            FirebaseCrashlytics.getInstance().recordException(e)
//            logAnalyticsError(" URL: $serverUrl Fehler beim Initialisieren von SSL", e)
//            logDataToFirestore(" URL: $serverUrl Fehler beim Initialisieren von SSL : $e")
//
//
//            throw e
//        }
//
//        return OkHttpClient.Builder()
//            .sslSocketFactory(sslContext.socketFactory, trustAllManager)
//            .hostnameVerifier { _, _ -> true }
//            .build()
//    }

//    fun checkConnection(context: Context, onResult:  (Boolean) -> Unit) {
//        initAnalytics(context)
//        Log.d("Result", "Checkconnection")
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val client = createSecureClient(context)
//                val url = serverUrl ?: return@launch onResult(false)
//
//                val request = Request.Builder().url(url).build()
//                val response = client.newCall(request).execute()
//
//                Log.d("HTTP", "Code: ${response.code}")
//                Log.d("HTTP", "After")
//                val bodyString = response.body?.string()
//                html = bodyString
//                if (bodyString != null) {
//                    logDataToFirestore(bodyString)
//                }
//                Log.d("HTTP", "HTML: $html")
//                onResult(true)
//            } catch (e: Exception) {
//                Log.e("HTTP", "Fehler: ${e.message}")
//                FirebaseCrashlytics.getInstance().log("Fehler bei der Secure Request")
//                FirebaseCrashlytics.getInstance().recordException(e)
//                logAnalyticsError("Fehler bei der Secure Request", e)
//                logDataToFirestore(" URL: $serverUrl Fehler bei der Secure Request: $e")
//
//
//                onResult(false)
//            }
//        }
//    }

        fun sendSecureRequest(context: Context, sendToast: Boolean, onResult: (Boolean) -> Unit) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val client = createSecureClient(context)
                    val url = serverUrl ?: return@launch

                    val request = Request.Builder().url(url).build()
                    val response = client.newCall(request).execute()

                    Log.d("DEBUG", "Code: ${response.code}")
                    withContext(Dispatchers.Main) {
                        if (sendToast) Toast.makeText(context, "Anfrage war erfolgreich", Toast.LENGTH_LONG).show()

                    }

                    val prefs = context.getSharedPreferences("cert_prefs", Context.MODE_PRIVATE)
                    Log.d("DEBUG", "SharedPreferences geladen")

                    prefs.edit()
                        .putString("server_url", url)
                        .putString("cert_uri", certUri?.toString())
                        .putString("cert_pwd", password)
                        .putString("alias", certAlias.value)
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
                        if (sendToast)  Toast.makeText(context, "Ein Fehler ist aufgetreten: ${e.message}", Toast.LENGTH_LONG).show()
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
                "cert_pwd" to prefs.getString("cert_pwd", "").orEmpty(),
                "alias" to prefs.getString("alias", "").orEmpty()
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
        requestCount = 0
        deviceCount = 0
        viewModelScope.launch {
            try {
                fetchHeizungData(context)
                fetchSolarData(context)
                fetchDeviceDatas(context)
            } catch (e: Exception)
            {
                logDataToFirestore("URL: $serverUrl Fehler bei fetchAllDatas : $e")
            }
        }
    }

    val openWindows = mutableListOf<String>()
    val openDoors = mutableListOf<String>()
    val unlockedDoors = mutableListOf<String>()



    fun updateOpenDoors() {
        viewModelScope.launch {
            dashboardRepository.updateOpenDoorDatas(openDoors.size.toString())

        }
    }

    fun updateClosedDoors() {
        viewModelScope.launch {
            dashboardRepository.updateClosedDoorDatas(unlockedDoors.size.toString())

        }
    }

    fun fetchSolarData(context: Context){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "${serverUrl}solar/opendtu"
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
                val url = "${serverUrl}visu/heizung/ajax/read_table_vicare_latest"
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

                dashboardRepository.updateHeizungValue("0", tempAussen)
                dashboardRepository.updateHeizungValue("1", modus)
                dashboardRepository.updateHeizungValue("2", brennerStunden)
                dashboardRepository.updateHeizungValue("3", brennerStarts)
                dashboardRepository.updateHeizungValue("4", timestamp)

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
                dashboardRepository.updateHeizungValue("0", "err")
                dashboardRepository.updateHeizungValue("1", "err")
                dashboardRepository.updateHeizungValue("2", "err")
                dashboardRepository.updateHeizungValue("3", "err")
                dashboardRepository.updateHeizungValue("4", "err")
            }
        }
    }

    fun fetchDeviceDatas(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val client = createSecureClient(context)
                val url = serverUrl ?: return@launch

                val groupList = groupRepository.getParsedGroupFlow().first()
                Log.d("Firebase Group", "Group exists: $groupList")

                groupList.forEach { group ->
                    processAllDevicesWithLimit(group, group.devices, client, url)
                }

                Log.d("ALLE GERÄTE AKTUALISIERT", "Alle Geräte aktualisiert")
                dashboardRepository.updateWindowDatas(groupRepository.getNumberOfOpenWindows().toString())
                dashboardRepository.updateOpenDoorDatas(groupRepository.getNumberOfOpenDoors().toString())
                dashboardRepository.updateClosedDoorDatas(groupRepository.getNumberOfUnlockedDoors().toString())



            } catch (e: Exception) {
                logDataToFirestore("URL: $serverUrl Fehler bei der sendSecureRequest : $e")

            }
        }
    }

    fun processDeviceListEntry(group: ParsedGroup, device: ParsedDevices, client: OkHttpClient, url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (device.deviceType) {
                  "D" -> processDelockDevice(group,  device, client, url)
                "F" -> processFritzDevice(group, device, client, url)
                "K" -> processKNXDevice(group, device, client, url)
                else -> Log.e("PARSER", "Invalid device type ${device.deviceType}")
            }
        }
    }


    suspend fun processAllDevicesWithLimit(
        group: ParsedGroup,
        devices: List<ParsedDevices>,
        client: OkHttpClient,
        url: String
    ) = coroutineScope {
        val semaphore = Semaphore(5)

        devices.forEach { device ->
            Log.d("Firebase Device TO FETCH", "Device exists: ${device.name}")
            launch {
                semaphore.withPermit {
                    Log.d("Firebase Device TO FETCH SEMAPHORE", "Device exists: ${device.name}")
                    processDeviceListEntry(group, device, client, url)
                    delay(100)
                }
            }
        }

    }


    suspend fun processKNXDevice(group: ParsedGroup, devices: ParsedDevices, client: OkHttpClient, url: String) {
        if (devices.adresse.startsWith("x") || devices.adresse.isEmpty()) {
            Log.d("KNX", "Adresse ignoriert: ${devices.adresse}")
            return
        }


        Log.d("ANZAHL DEVICES", "Anfrage Nr. ${++deviceCount}")


        Log.d("KNX", "-------------------------")
        Log.d("KNX", "➤ Gerät: ${devices.name} [${devices.htmlId}] Typ: ${devices.messwertTyp}")
        Log.d("KNX", "-------------------------")

        val id = devices.htmlId
        val messwertTyp = devices.messwertTyp
        val adresse = devices.adresse
        val dpt = devices.parameter1
        val name = devices.name
        val bfname = devices.bfname
        val maxAge = devices.maxAge

        val urlPath = if (messwertTyp == "TLFH") {
            val parts = adresse.split(";")
            val ga1 = URLEncoder.encode(parts.getOrNull(0) ?: return, "UTF-8")
            val ga2 = URLEncoder.encode(parts.getOrNull(1) ?: return, "UTF-8")
            val ga3 = parts.getOrNull(2)?.let { URLEncoder.encode(it, "UTF-8") }

            val encodedId = URLEncoder.encode(id, "UTF-8")

            buildString {
                append("knx/ajax/knx_read_temp_lfh?ga1=$ga1&ga2=$ga2&ID=$encodedId")
                if (!ga3.isNullOrEmpty()) append("&ga3=$ga3")
                if (maxAge.isNotEmpty() && maxAge != "0") append("&age=${URLEncoder.encode(maxAge, "UTF-8")}")
            }
        } else {
            val encodedAdresse = URLEncoder.encode(adresse, "UTF-8")
            val encodedDpt = URLEncoder.encode(dpt, "UTF-8")
            val encodedId = URLEncoder.encode(id, "UTF-8")
            val encodedName = URLEncoder.encode(name, "UTF-8")
            val encodedBfname = URLEncoder.encode(bfname, "UTF-8")

            buildString {
                append("knx/ajax/knx_read_ga?ga=$encodedAdresse&DPT=$encodedDpt&Type=$messwertTyp&ID=$encodedId&Name=$encodedName")
                if (bfname.isNotEmpty()) append("&bfname=$encodedBfname")
                if (maxAge.isNotEmpty() && maxAge != "0") append("&age=${URLEncoder.encode(maxAge, "UTF-8")}")
            }
        }

        val fullUrl = "$url$urlPath"
        Log.d("KNX", "Request-URL: $fullUrl")


        try {
            val request = Request.Builder().url(fullUrl).build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("KNX", "Fehlerhafte Antwort (${response.code}) bei $fullUrl")
                    return
                }
                if(response.isSuccessful)
                {
                    Log.d("ANZAHL RESPONSES", "Anfrage Nr. ${++requestCount}")
                }

                val json = response.body?.string()
                Log.d("KNX JSON ANTWORT", " Gerät: ${devices.name} Antwort JSON: $json")

                if (messwertTyp == "TLFH") {
                    if (!json.isNullOrEmpty()) {
                        try {
                            val jsonArray = JSONArray(json)
                            val value = jsonArray.optString(1)
                            val ids = id.split(";")
                            for (htmlId in ids) {
                                Log.d("KNX", "Setze $htmlId = $value")
                                groupRepository.updateDeviceValue(group.id, devices, value)
                            }
                        } catch (e: Exception) {
                            Log.e("KNX", "JSON Parse Fehler (TLFH): ${e.message}")
                        }
                    }
                } else {
                    if (!json.isNullOrEmpty()) {
                        try {
                            val jsonArray = JSONArray(json)
                            val value0 = jsonArray.optString(0)
                            val value1 = jsonArray.optString(1)
                            val ids = id.split(";")
                                val type = messwertTyp
                                val nameParam = name
                                Log.d("RESPONSES", "Verarbeite ${devices.name} $type: $nameParam mit Wert $value0/$value1")

                                when (type) {
                                    "R" -> {
                                        groupRepository.updateDeviceValue(group.id, devices, value0)
                                        Log.d("KNX", "Rollladen [$nameParam] → $value0")
                                    }
                                    "F" -> {
                                        if (value0 == "0") {
                                            if (!openWindows.contains(nameParam)) {
                                                Log.d("KNX", "Fenster [$nameParam] offen → hinzufügen")
                                                openWindows.add(nameParam)
                                                groupRepository.updateDeviceValue(group.id, devices, "0")
                                            }
                                        } else {
                                                Log.e("WINDOWS", "Fenster [$nameParam] geschlossen → entfernen")
                                                Log.e("WINDOWS", "IN")
                                                openWindows.remove(nameParam)
                                                Log.e("WINDOWS", "${devices.name}")
                                                groupRepository.updateDeviceValue(group.id, devices, "1")
                                        }
                                    }
                                    "T" -> {
                                        if (value0 == "0") {
                                            if (!openDoors.contains(nameParam)) {
                                                Log.d("KNX TÜREN OFFEN", "Tür [$nameParam] offen → hinzufügen")
                                                openDoors.add(nameParam)
                                                groupRepository.updateDeviceValue(group.id, devices, "0")
                                            }
                                        } else {

                                                Log.d("KNX TÜREN OFFEN", "Tür [$nameParam] geschlossen → entfernen")
                                                openDoors.remove(nameParam)
                                                groupRepository.updateDeviceValue(group.id, devices, "1")
                                        }

                                    }
                                    "V", "PR" -> {
                                        if (value0 == "0") {
                                            if (!unlockedDoors.contains(nameParam)) {
                                                Log.d("KNX", "Verriegelung [$nameParam] offen → hinzufügen")
                                                unlockedDoors.add(nameParam)
                                                groupRepository.updateDeviceValue(group.id, devices, "0")
                                            }
                                        } else {
                                                Log.d("KNX", "Verriegelung [$nameParam] geschlossen → entfernen")
                                                unlockedDoors.remove(nameParam)
                                                groupRepository.updateDeviceValue(group.id, devices, "1")

                                        }

                                    }
                                    else -> {
                                        groupRepository.updateDeviceValue(group.id, devices, value0)
                                        Log.d("KNX", "Standardwert [$nameParam] → $value1")
                                    }
                                }
                        } catch (e: Exception) {
                            Log.e("KNX", "JSON Parse Fehler (Normal): ${e.message}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("KNX", "Request-Fehler: ${e.message}")
        }

        Log.d("KNX", "Fertig verarbeitet: ${devices.name} (${devices.htmlId})")
    }


    suspend fun processFritzDevice(
        group: ParsedGroup,
        devices: ParsedDevices,
        client: OkHttpClient,
        url: String
    ) {
        val requestUrl = "${url}visu/heizung/ajax/get_latest_temp_from_db.php?identifier=${devices.deviceId}&type=dect&ID=${group.id}"

        Log.d("DEBUG FETCH", "FRITZ Device $devices")

        val request = Request.Builder().url(requestUrl).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.e("FRITZ ERROR", "Unexpected code $response")
                groupRepository.updateDeviceValue(group.id, devices, "err")
                return
            }

            val responseBody = response.body?.string() ?: return
            val data2 = JSONArray(responseBody)
            val temp = data2.getJSONObject(0).getString("Ist_Temp")
            groupRepository.updateDeviceValue(group.id, devices, temp)
        }
    }

    suspend fun processDelockDevice(
        group: ParsedGroup,
        devices: ParsedDevices,
        client: OkHttpClient,
        url: String
    ) {
        val requestUrl = "${url}delock/ajax/read_delock_state.php?device=${devices.adresse}&ID=${devices.htmlId}&bfname=${devices.bfname}"

        Log.d("DEBUG FETCH", "DELOCK Device $devices")

        val request = Request.Builder().url(requestUrl).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.e("DELOCK ERROR", "Unexpected code $response")
                groupRepository.updateDeviceValue(group.id, devices, "err")
                return
            }

            val responseBody = response.body?.string() ?: return
            val data33 = JSONArray(responseBody)
            groupRepository.updateDeviceValue(group.id, devices, data33.getString(0))

            }
        }

//    suspend fun delockSwitchAndUpdate(
//        context: Context,
//        device: ParsedDevices,
//        value: String,
//        groupId: Int,
//    ) {
//        val client = createSecureClient(context)
//        val config = getServerConfig(context)
//        val url = config["server_url"]
//
//
//        val requestUrl = "$url/sh/delock/ajax/write_delock_state.php?device=${device.adresse}&value=$value&ID=${device.htmlId}"
//                val request = Request.Builder()
//                    .url(requestUrl)
//                    .post(FormBody.Builder().build())
//                    .build()
//
//                client.newCall(request).execute().use { response ->
//                    if (!response.isSuccessful) {
//                        Log.e("DELOCK SWITCH", "Unexpected code $response")
//                        return
//                    }
//
//                    val responseBody = response.body?.string() ?: return
//                    val delockData = JSONObject(responseBody)
//                        if (delockData.optString("POWER") == "ON") {
//                            groupRepository.updateDeviceValue(groupId.toString(), device, "1")
//                        } else {
//                            groupRepository.updateDeviceValue(groupId.toString(), device, "0")
//                        }
//                    }
//        }

}