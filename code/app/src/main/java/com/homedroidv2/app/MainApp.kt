package com.homedroidv2.app

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.car.app.connection.CarConnection
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homedroidv2.app.screens.MainScreen
import com.homedroidv2.app.screens.ServerKonfigScreen
import com.homedroidv2.app.screens.SplashScreen
import com.homedroidv2.app.viewmodel.ServerConfigViewModel
import com.homedroidv2.data.login.Login
import com.homedroidv2.data.parser.HtmlParser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Hauptkomponente der Anwendung, zuständig für den App-Start und die Initialkonfiguration.
 *
 * Diese Klasse implementiert die zentrale Logik zum Aufbau der Benutzeroberfläche und verwaltet die Navigation
 * zwischen den verschiedenen Screens (Splash, Server-Konfiguration, MainScreen).
 */
@AndroidEntryPoint
class MainApp : ComponentActivity() {

    @Inject lateinit var login: Login
    @Inject lateinit var htmlParser: HtmlParser

    private val splashActivity = SplashScreen()
    private val mainActivity = MainScreen()
    //private val loginScreen = LoginScreen()
    private val serverKonfigScreen = ServerKonfigScreen()

    private var html: String = ""

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Log.d("DEBUG", "APP STARTED")
            val viewModel: ServerConfigViewModel = viewModel()
            val carConnectionType by CarConnection(this).type.observeAsState(initial = -1)

            var showSplash by remember { mutableStateOf(true) }
            var isParsing by remember { mutableStateOf(false) }
            var serverKonfigCompleted by remember { mutableStateOf(false) }
            var htmlIsLoaded by remember { mutableStateOf(false) }
            var initCheckDone by remember { mutableStateOf(false) }

            val config = viewModel.getServerConfig(this)
            val serverUrl = config["server_url"]
            config["cert_uri"]
            config["cert_pwd"]
            val alias = config["alias"]
            Log.d("DEBUG MAIN", "SERVER URL: $serverUrl")
            Log.d("DEBUG MAIN", "ALIAS: $alias")


            LaunchedEffect(Unit) {
                if (alias != null) {
                    Log.d("DEBUG MAIN", "ALIAS 1: $alias")
                    if (!serverUrl.isNullOrEmpty() && !alias.isNullOrEmpty()) {
                        Log.d("DEBUG MAIN", "ALIAS 2: $alias")
                        viewModel.checkConnection(this@MainApp, serverUrl, alias) { success ->
                            if (success) {
                                Log.d("Result", "Connection successful in Main")
                                html = viewModel.html.orEmpty()
                                serverKonfigCompleted = true
                                viewModel.fetchAllDatas(this@MainApp)

                            }
                            initCheckDone = true
                        }
                    } else {
                        initCheckDone = true
                    }
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
                                htmlParser.checkHtmlChanges(html){ result ->
                                    if(result)
                                    {
                                        viewModel.fetchAllDatas(this@MainApp)
                                        htmlIsLoaded = true

                                    }else
                                    {
                                        //viewModel.fetchAllDatas(this@MainApp)
                                        htmlIsLoaded = false

                                    }
                                }
                            }
                            isParsing = false
                            showSplash = false
                        }

                        if (showSplash) {
                            splashActivity.SplashScreen(isParsing) {}
                        } else {
                            mainActivity.MainScreen(this, carConnectionType, htmlIsLoaded)
                        }
                    }
                }
            }
        }
    }
}