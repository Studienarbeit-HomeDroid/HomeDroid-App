package com.homedroid.app

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.car.app.connection.CarConnection
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import com.homedroid.app.screens.LoginScreen
import com.homedroid.app.screens.MainScreen
import com.homedroid.app.screens.ServerKonfigScreen
import com.homedroid.app.screens.SplashScreen
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

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = this.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        setContent {
            val carConnectionType by CarConnection(this).type.observeAsState(initial = -1)
            var showSplash by remember { mutableStateOf(true) }
            var isParsing by remember { mutableStateOf(false) }
            var isLoggedIn by remember { mutableStateOf(false) }
            var loginChecked by remember { mutableStateOf(false) }
            var serverKonfigCompleted by remember { mutableStateOf(false) }
            var htmlIsLoaded by remember { mutableStateOf(false) }

            val token: String = sharedPreferences.getString("token", null).orEmpty()
            Log.i("MainApp", "Token: $token")

            // Login-Check bei App-Start
            LaunchedEffect(Unit) {
                if (token.isNotEmpty()) {
                    withContext(Dispatchers.IO) {
                        isLoggedIn = login.getProtectedDataSync(token)
                    }
                }
                loginChecked = true
            }

            when {
                !loginChecked -> {
                    splashActivity.SplashScreen(isParsing = false) {}
                }

                !isLoggedIn -> {
                    isLoggedIn = loginScreen.LoginComponent()
                }

//                !serverKonfigCompleted -> {
//                    serverKonfigScreen.ServerKonfig {
//                        serverKonfigCompleted = true
//                    }
//                }

                isLoggedIn-> {
                    LaunchedEffect(Unit) {
                        isParsing = true
                        withContext(Dispatchers.IO) {
                            htmlIsLoaded = htmlParser.checkHtmlChanges(token)
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