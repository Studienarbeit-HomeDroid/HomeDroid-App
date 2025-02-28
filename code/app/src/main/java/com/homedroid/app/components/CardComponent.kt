package com.homedroid.app.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homedroid.app.presentation.FavoriteViewModel
import com.homedroid.data.model.Device

class CardComponent {

    /**
     * Displays Temperature and Status Device Card for the Favorite Section
     * @param device The device object to be displayed, which can be either a StatusDevice or a TemperatureDevice.
     * @param viewModel The ViewModel responsible for managing favorite devices. Defaults to the current `FavoriteViewModel`.
     *
     * Interactions:
     * - Click: Reserved for future functionality (currently no action).
     * - Long Click:
     *   - Displays a dialog to confirm the removal of the device as a favorite.
     *   - Provides haptic feedback via vibration.
     */
    @OptIn(ExperimentalFoundationApi::class)
    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun TempAndStatusDeviceCard(device: Device, viewModel: FavoriteViewModel = viewModel()) {
        var showDialog by remember { mutableStateOf(false) }
        val context = LocalContext.current
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        ElevatedCard(
            modifier = Modifier
                .size(105.dp)
                .padding(5.dp)
                .combinedClickable(
                    onClick = {
                        /**Only Need because of the attributes of "combinedClickable"*/
                    },
                    onLongClick = {
                        showDialog = true
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(
                                VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                            )
                        }
                    }
                ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    if (device is Device.StatusDevice) {
                        Text(
                            text = device.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp)
                                .padding(top = 3.dp),
                            textAlign = TextAlign.Start
                        )
                        Text(
                            text = device.group,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp)
                                .padding(top = 3.dp),
                            textAlign = TextAlign.Start
                        )
                    }else if (device is Device.TemperatureDevice)
                    {
                        Text(
                            text = device.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp)
                                .padding(top = 3.dp),
                            textAlign = TextAlign.Start
                        )

                        Text(
                            text = device.group,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp)
                                .padding(top = 3.dp),
                            textAlign = TextAlign.Start
                        )
                    }
                }
                Text(
                    text = when (device) {
                        is Device.StatusDevice -> "${device.value} ${device.unit}"
                        is Device.TemperatureDevice -> "${device.value} °C"
                        else -> "Unknown Type"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                        .padding(bottom = 5.dp),
                    textAlign = TextAlign.Start
                )
            }
        }
        /**
         * Dialog to confirm the removal of the device as a favorite
         */
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Favorite",
                        tint = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth(0.6F)
                    )
                },
                title = {
                    Text("Favorit entfernen?")
                },
                confirmButton = {
                    Button(onClick = {
                        if (device is Device.StatusDevice || device is Device.TemperatureDevice) {
                            viewModel.removeFavorite(device)
                        }
                        showDialog = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialog = false
                    }) {
                        Text("Zurück")
                    }
                }
            )
        }
    }

    /**
     * Displays Action Device Card for the Favorite and Status Sections of the different Groups
     * @param device The device object to be displayed, which must be an ActionDevice.
     * @param viewModel The ViewModel responsible for managing favorite devices. Defaults to the current `FavoriteViewModel`.
     *
     * Interactions:
     * - Click: Toggles the state of the device between "on" and "off".
     * - Long Click:
     *   - Opens a dialog to add or remove the device as a favorite.
     *   - Provides haptic feedback via vibration.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ActionDeviceCard(device: Device.ActionDevice, viewModel: FavoriteViewModel = viewModel()) {
        val context = LocalContext.current
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        var showDialog by remember { mutableStateOf(false) }
        var isToggled by remember { mutableStateOf(device.status) }

        Box(
            modifier = Modifier
                .size(105.dp)
                .padding(5.dp)
                .combinedClickable(
                    onClick = {
                        isToggled = !isToggled
                    },
                    onLongClick = {
                        showDialog = true
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(
                                VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                            )
                        }
                    }

                ),
            contentAlignment = Alignment.TopStart
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.elevatedCardElevation(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isToggled) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 10.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = device.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 3.dp),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "On",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 3.dp),
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
        if (showDialog) {
            AlertDialog(
                modifier = Modifier.blur(
                    30.dp,
                    30.dp,
                    BlurredEdgeTreatment(RoundedCornerShape(13.dp))
                ),
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.95F),
                onDismissRequest = { showDialog = false },
                icon = {
                    if (viewModel.isFavorite(device)) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Favorite",
                            tint = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth(0.6F)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth(0.6F)
                        )
                    }
                },
                title = {
                    if (viewModel.isFavorite(device)) Text("Favorit entfernen") else Text("Favorit hinzufügen")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            if (viewModel.isFavorite(device)) {
                                viewModel.removeFavorite(device)
                            } else {
                                viewModel.addFavorite(device)
                            }
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialog = false
                    }) {
                        Text("Zurück")
                    }
                }
            )
        }
    }


}
