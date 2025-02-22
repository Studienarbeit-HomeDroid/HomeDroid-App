package com.homedroid.data.network

import IconResponse
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlinx.serialization.json.Json


class HtmlClient {

    private val client = OkHttpClient()
    private val baseUrl = "http://192.168.178.200:3000"

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