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

    /**
     * Displays a ModalBottomSheet for a specific group.
     *
     * @param openButtomSheet The ViewModel controlling the sheet's visibility.
     * @param group The group whose details and devices are displayed in the sheet.
     */

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
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(top = 13.dp)
                )
                if(group.devices.filterIsInstance<Device.TemperatureDevice>().isNotEmpty()) {
                    Text(
                        text = "Temperatur",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    LazyColumn {
                        items(group.devices.filterIsInstance<Device.TemperatureDevice>()) { devices ->
                            DeviceRow(devices)
                        }
                    }
                }
                Text(
                    text = "Status",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                LazyColumn {
                    items(group.devices.filterIsInstance<Device.StatusDevice>()) { devices ->
                        DeviceRow(devices)
                    }
                }
                ActionDevicesRow(group.devices)
            }
        }
    }

    /**
     * Creates a row of action devices.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun ActionDevicesRow(items: List<Device>) {
        val favoriteCardComponent = CardComponent()

        Column{
            items.chunked(4).forEach { rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowItems.forEach { item ->
                        if (item is Device.ActionDevice) {
                            favoriteCardComponent.ActionDeviceCard(item)
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates a row for a Status and Temperature device.
     */
    @OptIn(DelicateCoroutinesApi::class)
    @Composable
    fun DeviceRow(device: Device, viewModel: FavoriteViewModel = viewModel()) {
        var swipeOffset by remember { mutableStateOf(0f) }
        var isFavorite by remember { mutableStateOf(false) }

        /**
         * Point of show the favorite button
         */
        val swipeThreshold = 60f

        /**
         * Maximum swipe distance
         */
        val maxSwipe = 80f

        /**
         * - Uses `pointerInput` to detect horizontal drag gestures:
         *  - Updates `swipeOffset` based on the drag amount.
         *  - Constrains the offset to stay within -maxSwipe and 0.
         */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .padding(6.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        swipeOffset = (swipeOffset + dragAmount).coerceIn(-maxSwipe, 0f)
                    }
                }
        ) {
            when (device) {
                is Device.StatusDevice -> {
                    RowContent(
                        name = device.name,
                        value = device.value,
                        unit = device.unit,
                        swipeOffset
                    )
                }
                is Device.TemperatureDevice -> {
                    RowContent(
                        name = device.name,
                        value = device.value,
                        unit = "°C",
                        swipeOffset
                    )
                }

                is Device.ActionDevice -> {}
            }

            /**
             * If the swipe offset is less than -swipeThreshold, the favorite button is shown
             */
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
                            } else {
                                viewModel.addFavorite(device)
                                Log.i("DEVICES CLICKED", device.toString())

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
                        modifier = Modifier.fillMaxWidth(0.6F)
                    )
                }
            }
        }
    }


    @Composable
    fun RowContent(name: String, value: String, unit: String, swipeOffset: Float) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = swipeOffset.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                )

            Text(
                text = "$value $unit",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}



