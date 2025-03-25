package com.homedroid.data.network

import IconResponse
import android.util.Log
import com.google.android.gms.common.api.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class HtmlClient {

    private val localUrl = "http://192.168.178.200:3000"
    private val remoteUrl = "https://homedroid-server.onrender.com/"
    private val client = OkHttpClient()
    private val baseUrl = remoteUrl

    /**
     * @return Der HTML-Content als String oder null bei einem Fehler.
     */
    suspend fun getHtml(): String? {
        val request = Request.Builder()
            .url(baseUrl)
            .build()

        return try {
            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }
            if (response.isSuccessful) {
                response.body?.string()
            } else {
                null
            }
        } catch (e: IOException) {
            Log.i("DATA FROM SERVER", "Request failed with code: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    suspend fun getProtectedData(token: String): String? {
        Log.i("DATA FROM SERVER", "getProtectedData: $token")
        val request = Request.Builder()
            .url("$baseUrl" + "protected")
            .addHeader("Authorization", "Bearer $token")
            .build()
        Log.i("DATA FROM SERVER", "getProtectedData: $request")

         try {
            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            if (response.isSuccessful) {
                return response.body?.string()
            } else {
                return  null
            }
        } catch (e: Exception) {
            Log.e("NetworkError", "Fehler bei der Anfrage: ${e.message}")
            return null
        }
    }

    fun extractTokenFromJson(responseBody: String): String? {
        return try {
            val jsonObject = JSONObject(responseBody)
            jsonObject.optString("token", null)
        } catch (e: Exception) {
            Log.e("Login", "Fehler beim Parsen des Tokens: ${e.message}")
            null
        }
    }

    suspend fun logIn(username: String, password: String): String? {
        if (username.isBlank() || password.isBlank()) {
            return null
        }

        val requestBody = """
        {
            "username": "$username",
            "password": "$password"
        }
    """.trimIndent()
        Log.i("LOGIN", requestBody)

        val request = Request.Builder()
            .url(baseUrl + "login")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()


        return try {
            val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val token = responseBody?.let { extractTokenFromJson(it) }
                token
            } else {
                val errorMessage = response.body?.string() ?: "Unbekannter Fehler"
                Log.e("Login", "Fehler beim Login: $errorMessage")
                null
            }
        } catch (e: Exception) {
            Log.e("Login", "Fehler bei der Anfrage: ${e.message}")
            null
        }
    }





    suspend fun getIcon(icon: String): String? {
        val iconfinderApiUrl =
            "https://api.iconfinder.com/v4/icons/search?query=${icon}&count=1&premium=false&style=filled-outline"
        Log.i("API URL",iconfinderApiUrl)
        val bearerToken = "8g6VEeStqwc9Wyge9ZX9z9VfMsQQH8INco74FIrQsv3BsprZudWkFdKJlPduwi1D"
        val request = Request.Builder()
            .header("Authorization", "Bearer $bearerToken")
            .url(iconfinderApiUrl)
            .build()

        try {
            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }
            if (response.isSuccessful) {
                response.body?.use { responseBody ->
                    val responseString = responseBody.string()
                    try {
                        val iconResponse = Json.decodeFromString<IconResponse>(responseString)

                        Log.i("ICONS", responseString)
                        return iconResponse.icons
                            .flatMap { it.vectorSizes }
                            .flatMap { it.formats }
                            .find { it.format == "svg" }
                            ?.downloadUrl


                    } catch (e: Exception) {
                        Log.e("ICONS", "Error parsing JSON: ${e.message}")
                        return null
                    }
                }
            } else {
                Log.e("ICONS", "Request failed with code: ${response.code}")
                return null
            }

        } catch (e: IOException) {
            Log.e("DATA FROM SERVER", "IOException: ${e.message}", e)
            return null
        }
        return null
    }
}