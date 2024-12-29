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

package com.example.places

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.car.app.connection.CarConnection
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.places.data.parser.HtmlParser
import com.example.places.screens.MainScreen
import com.example.places.screens.SplashScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainApp : ComponentActivity() {
    private val splashActivity: SplashScreen = SplashScreen()
    private val mainActivity: MainScreen = MainScreen()
    private val htmlParser: HtmlParser = HtmlParser()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val carConnectionType by CarConnection(this).type.observeAsState(initial = -1)
            var showSplash by remember { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    htmlParser.parseHtml() // Warten, bis der Parser fertig ist
                }
                showSplash = false // Splash-Screen beenden, nachdem der Parser fertig ist
            }

            if (showSplash) {
                splashActivity.SplashScreen {
                    // Keine direkte Änderung von `showSplash` hier
                }
            } else {
                mainActivity.MainContent(carConnectionType)
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