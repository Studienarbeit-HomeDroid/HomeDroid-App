/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.homedroid.app

import android.content.Context
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
import com.homedroid.app.screens.LoginScreen
import com.homedroid.data.model.Group
import com.homedroid.data.parser.HtmlParser
import com.homedroid.data.repositories.GroupRepository
import com.homedroid.app.screens.MainScreen
import com.homedroid.app.screens.SplashScreen
import com.homedroid.data.login.Login
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Main application activity responsible for initializing and managing the app's UI and core processes.
 */

@AndroidEntryPoint
class MainApp : ComponentActivity() {
    @Inject
    lateinit var login: Login

    @Inject
    lateinit var htmlParser: HtmlParser

    private val splashActivity: SplashScreen = SplashScreen()
    private var htmlIsLoaded: Boolean = false
    private val mainActivity: MainScreen = MainScreen()
    //private val groupRepository: GroupRepository = GroupRepository()
    private val loginScreen: LoginScreen = LoginScreen()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = this.getSharedPreferences("TokenPrefs", Context.MODE_PRIVATE)

        setContent {
            val carConnectionType by CarConnection(this).type.observeAsState(initial = -1)
            var showSplash by remember { mutableStateOf(true) }
            var isParsing by remember { mutableStateOf(false) }
            //var groups by remember { mutableStateOf(emptyList<Group>()) }
            var isLoggedIn by remember { mutableStateOf(false) }
            var loginChecked by remember { mutableStateOf(false) } // Neuer State, um das Laden zu steuern

            val token: String = sharedPreferences.getString("token", null).orEmpty()
            Log.i("DATA FROM SERVER", "onCreate: $token")

            LaunchedEffect(token) {
                if (token.isNotEmpty()) {
                    withContext(Dispatchers.IO) {
                        isLoggedIn = login.getProtectedDataSync(token)
                        Log.i("DATA FROM SERVER", "isLoggedIn: $isLoggedIn")
                    }
                }
                loginChecked = true
            }

            if (!loginChecked) {
                splashActivity.SplashScreen(isParsing) {}
            } else if (isLoggedIn) {
                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) {
                        isParsing = true
                        htmlIsLoaded = htmlParser.checkHtmlChanges(token)

                    }
                    isParsing = false
                    showSplash = false
                }

                if (showSplash) {
                    splashActivity.SplashScreen(isParsing) {}
                } else {
                    Log.i("PARSER", "type: $htmlIsLoaded")
                    mainActivity.MainScreen(carConnectionType, htmlIsLoaded)
                }
            } else {
                isLoggedIn = loginScreen.LoginComponent()
            }
        }
    }
}


//@Composable
//fun PlaceList(places: List<Place>) {
//    val context = LocalContext.current
//    LazyColumn {
//        items(places.size) {
//            val place = places[it]
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp)
//                    .border(
//                        2.dp,
//                        color = MaterialTheme.colorScheme.outline,
//                        shape = RoundedCornerShape(8.dp)
//                    )
//                    .clip(RoundedCornerShape(8.dp))
//                    .clickable {
//                        context.startActivity(place.toIntent(Intent.ACTION_VIEW))
//                    }
//                    .padding(8.dp)
//            ) {
//                Icon(
//                    Icons.Default.Place,
//                    "Place icon",
//                    modifier = Modifier.align(CenterVertically),
//                    tint = MaterialTheme.colorScheme.tertiary
//                )
//                Column {
//                    Text(
//                        text = place.name,
//                        style = MaterialTheme.typography.labelLarge
//                    )
//                    Text(
//                        text = place.description,
//                        style = MaterialTheme.typography.bodyMedium,
//                        overflow = TextOverflow.Ellipsis,
//                        maxLines = 2
//                    )
//                }
//
//            }
//         }
//     }
//}