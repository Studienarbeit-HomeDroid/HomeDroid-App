package com.homedroid.app.components

import BottomSheetViewModel
import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.car.app.connection.CarConnection
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.homedroid.data.model.Group
import com.homedroid.app.ui.theme.HomeDroidTheme
import com.homedroid.app.viewmodel.GroupViewModel
import com.homedroid.data.model.ParsedGroup

class GroupComponent {

    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun GroupList(carConnectionType: Int,  htmlIsLoaded: Boolean, viewModel: GroupViewModel = viewModel()) {
        Log.i("GroupList", "In Group List")
        val groupsFlowList = viewModel.groups.collectAsState()
        val context = LocalContext.current

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Gruppen",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(top = 13.dp)
                    .padding(bottom = 8.dp)
            )

            ProjectionState(
                carConnectionType = carConnectionType,
                modifier = Modifier.padding(8.dp)
            )
        }

            LazyRow(
                modifier = Modifier.padding(end = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                groupsFlowList.value.forEach { group ->
                    item {
                        Group(group)
                    }
                }
            }

        if (!htmlIsLoaded) {
            Handler(Looper.getMainLooper()).postDelayed({
                Toast.makeText(context, "Der Server ist momentan nicht erreichbar. Möglicherweise ist die Benutzeroberfläche veraltet.", Toast.LENGTH_LONG).show()
            }, 3000) // 3000 Millisekunden = 3 Sekunden
        }



    }


    @SuppressLint("NewApi")
    @Composable
    fun Group(group: ParsedGroup) {
        val openBottomSheet = remember { BottomSheetViewModel() }
        val modalButtomSheetComponent = ModalButtomSheetComponent()
        val context = LocalContext.current

        // ImageRequest für das Gruppen-Icon
        val imageRequest = ImageRequest.Builder(context)
            .setHeader(
                "Authorization",
                "Bearer 8g6VEeStqwc9Wyge9ZX9z9VfMsQQH8INco74FIrQsv3BsprZudWkFdKJlPduwi1D"
            )
            .data(group.iconUrl)
            .memoryCacheKey(group.iconUrl)
            .diskCacheKey(group.iconUrl)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()

        HomeDroidTheme {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(5.dp)
            ) {
                // Card-Layout für Gruppenbild und Namen
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .size(width = 70.dp, height = 70.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            openBottomSheet.toggleBottomSheet()
                        },
                    elevation = CardDefaults.elevatedCardElevation()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
//                        if (!group.iconUrl.isNullOrBlank()) {
//                            AsyncImage(
//                                model = imageRequest,
//                                contentDescription = "Icon from URL",
//                                modifier = Modifier.size(30.dp),
//                            )
//                            Log.i("ASYNCIMAGE LOADED", group.id.toString())
//                        } else {
//                            Icon(
//                                imageVector = Icons.Outlined.Place,
//                                contentDescription = "Icon from URL",
//                                modifier = Modifier.size(30.dp)
//                            )
//                        }

                        Text(
                            text = group.name.firstOrNull()?.toString()?.take(1) ?: "D", // Holt den ersten Buchstaben oder einen Default-Wert ("D")
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                    }
                }

                Text(
                    text = group.name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .widthIn(max = 70.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Zeige das BottomSheet für die Gruppe
            if (openBottomSheet.getBottomSheetValue()) {
                modalButtomSheetComponent.ModalButtomSheet(openBottomSheet, group)
            }
        }
    }

    // Funktion zur Anzeige des aktuellen Projektionsstatus
    @Composable
    fun ProjectionState(carConnectionType: Int, modifier: Modifier = Modifier) {
        val text = when (carConnectionType) {
            CarConnection.CONNECTION_TYPE_NOT_CONNECTED -> "Not projecting"
            CarConnection.CONNECTION_TYPE_NATIVE -> "Running on Android Automotive OS"
            CarConnection.CONNECTION_TYPE_PROJECTION -> "Projecting"
            else -> "Unknown connection type"
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            modifier = modifier.padding(5.dp)
        )
    }
}