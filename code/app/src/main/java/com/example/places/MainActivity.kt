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

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.car.app.connection.CarConnection
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.places.components.DashboardComponent
import com.example.places.components.FavoriteComponents
import com.example.places.components.GroupComponent
import com.example.places.data.GroupRepository
import com.example.places.ui.theme.HomeDroidTheme

class MainActivity : ComponentActivity() {
    private val groupComponent: GroupComponent = GroupComponent()
    private val dashboardComponent: DashboardComponent = DashboardComponent()
    private val favoriteComponent: FavoriteComponents = FavoriteComponents()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val carConnectionType by CarConnection(this).type.observeAsState(initial = -1)

            HomeDroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().verticalScroll(ScrollState(1000)),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                        groupComponent.GroupList(carConnectionType, groups = GroupRepository().getGroupItems())
                        dashboardComponent.Dashboard()
                        favoriteComponent.Favorite()
                        //PlaceList(places = PlacesRepository().getPlaces())
                    }
                }
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