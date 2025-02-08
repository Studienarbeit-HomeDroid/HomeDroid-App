package com.example.places.components

import BottomSheetViewModel
import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.places.presentation.FavoriteViewModel
import com.example.places.data.model.Device
import com.example.places.data.model.Group
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ModalButtomSheetComponent {

    @RequiresApi(Build.VERSION_CODES.Q)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ModalButtomSheet(openButtomSheet: BottomSheetViewModel, group: Group) {
        ModalBottomSheet(
            onDismissRequest = { openButtomSheet.toggleBottomSheet() },
            modifier = Modifier.fillMaxHeight(),
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 15.dp)
                    .padding(end = 15.dp)
                    .padding(bottom = 5.dp)

            ) {
                Text(
                    text = group.name,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(top = 13.dp)
                )
                Text(
                    text = "Geräte",
                    textAlign = TextAlign.Center,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                LazyColumn {
                    items(group.devices) { devices ->
                        DeviceRow(devices)
                    }
                }
                Text(
                    text = "Status",
                    textAlign = TextAlign.Center,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)

                )
                ActionDevicesRow(group.devices)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun ActionDevicesRow(items: List<Device>) {
        val favoriteCardComponent: CardComponent = CardComponent()

        Column(
        ) {
            items.chunked(4).forEach { rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowItems.forEach { item ->
                        if (item is Device.ActionDevice) {
                            favoriteCardComponent.ActionCard(item)
                        }
                    }
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Composable
    fun DeviceRow(device: Device, viewModel: FavoriteViewModel = viewModel()) {
        if (device is Device.StatusDevice) {
            var swipeOffset by remember { mutableStateOf(0f) }
            var isFavorite by remember { mutableStateOf(false) }
            val swipeThreshold = 60f // Schwelle für das vollständige Wischen
            val maxSwipe = 80f // Maximale Verschiebung

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .padding(6.dp)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { _, dragAmount ->
                            swipeOffset = (swipeOffset + dragAmount).coerceIn(
                                -maxSwipe,
                                0f
                            )
                        }
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(x = swipeOffset.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Plus Icon",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = device.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(start = 40.dp)
                                .align(Alignment.CenterStart)
                        )
                    }
                    Text(
                        text = "${device.value} ${device.unit}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                if (swipeOffset <= -swipeThreshold) {
                    Box(
                        modifier = Modifier
                            .height(50.dp)
                            .width(50.dp)
                            .align(Alignment.CenterEnd)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                if (viewModel.isFavorite(device)) {
                                    viewModel.removeFavorite(device)
                                    Log.i(
                                        "ADD TO FAVORITES",
                                        viewModel.favorites.value
                                            .count()
                                            .toString()
                                    )

                                } else {
                                    viewModel.addFavorite(device)
                                    Log.i(
                                        "ADD TO FAVORITES",
                                        viewModel.favorites.value
                                            .count()
                                            .toString()
                                    )
                                }
                                isFavorite = !isFavorite
                                GlobalScope.launch {
                                    delay(1000)
                                    swipeOffset = 0F
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = if (viewModel.isFavorite(device)) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (viewModel.isFavorite(device)) "Favorite" else "No Favorite",
                            tint = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth(0.6F)
                        )
                    }
                }
            }
        }
    }
}



