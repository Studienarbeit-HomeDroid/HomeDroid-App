package com.homedroid.app

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.car.app.connection.CarConnection
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homedroid.app.screens.LoginScreen
import com.homedroid.app.screens.MainScreen
import com.homedroid.app.screens.ServerKonfigScreen
import com.homedroid.app.screens.SplashScreen
import com.homedroid.app.viewmodel.ServerConfigViewModel
import com.homedroid.data.login.Login
import com.homedroid.data.parser.HtmlParser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainApp : ComponentActivity() {

    @Inject lateinit var login: Login
    @Inject lateinit var htmlParser: HtmlParser

    private val splashActivity = SplashScreen()
    private val mainActivity = MainScreen()
    private val loginScreen = LoginScreen()
    private val serverKonfigScreen = ServerKonfigScreen()

    private var html: String = ""

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: ServerConfigViewModel = viewModel()
            val carConnectionType by CarConnection(this).type.observeAsState(initial = -1)

            var showSplash by remember { mutableStateOf(true) }
            var isParsing by remember { mutableStateOf(false) }
            var loginChecked by remember { mutableStateOf(false) }
            var serverKonfigCompleted by remember { mutableStateOf(false) }
            var htmlIsLoaded by remember { mutableStateOf(false) }
            var initCheckDone by remember { mutableStateOf(false) }

            val config = viewModel.getServerConfig(this)
            val serverUrl = config["server_url"]
            val certUri = config["cert_uri"]
            val certPwd = config["cert_pwd"]

            // Initiale Verbindungsprüfung einmalig durchführen
            LaunchedEffect(Unit) {
                if (!serverUrl.isNullOrEmpty() && !certUri.isNullOrEmpty() && !certPwd.isNullOrEmpty()) {
                    viewModel.setConfig(serverUrl, Uri.parse(certUri), certPwd)
                    viewModel.checkConnection(this@MainApp) { success ->
                        if (success) {
                            Log.d("Result", "Connection successful in Main")
                            html = viewModel.html.orEmpty()
                            serverKonfigCompleted = true
                        }
                        initCheckDone = true
                    }
                } else {
                    initCheckDone = true
                }
            }

            if (!initCheckDone) {
                splashActivity.SplashScreen(isParsing = false) {}
            } else {
                when {
                    !serverKonfigCompleted -> {
                        serverKonfigScreen.ServerKonfig(context = this) {
                            html = viewModel.html.orEmpty()
                            serverKonfigCompleted = true
                        }
                    }

                    serverKonfigCompleted -> {
                        LaunchedEffect(Unit) {
                            isParsing = true
                            withContext(Dispatchers.IO) {
                                htmlIsLoaded = htmlParser.checkHtmlChanges(html)
                            }
                            isParsing = false
                            showSplash = false
                        }

                        if (showSplash) {
                            splashActivity.SplashScreen(isParsing) {}
                        } else {
                            mainActivity.MainScreen(carConnectionType, htmlIsLoaded)
                        }
                    }
                }
            }
        }
    }
}