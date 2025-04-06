package com.homedroidv2.app.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.homedroidv2.app.R
import com.homedroidv2.app.viewmodel.FavoriteViewModel
import com.homedroidv2.app.viewmodel.GroupViewModel
import com.homedroidv2.app.viewmodel.ServerConfigViewModel
import com.homedroidv2.data.model.ParsedDevices
import com.homedroidv2.data.model.ParsedGroup

class CardComponent {

    /**
     * Erstellt eine Karte für Geräte, mit denen nicht interagiert werden kann,
     * zur Darstellung innerhalb der Favoritenansicht.
     *
     * Die Funktion nimmt ein `ParsedGroup`- und ein `ParsedDevice`-Objekt als Parameter entgegen,
     * welche zur Bearbeitung der zugehörigen Einträge in der Datenbank verwendet werden.
     *
     * Zur sauberen Trennung von UI und Logik werden zusätzlich zwei ViewModels übergeben,
     * über die der Daten- und Zustandszugriff erfolgt.
     */

    @OptIn(ExperimentalFoundationApi::class)
    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun TempAndStatusDeviceCard( group: ParsedGroup, device: ParsedDevices, viewModel: FavoriteViewModel = viewModel(), groupViewModel: GroupViewModel = viewModel()) {
        var showDialog by remember { mutableStateOf(false) }
        val context = LocalContext.current
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        ElevatedCard(
            modifier = Modifier
                .size(105.dp)
                .padding(5.dp)
                .combinedClickable(
                    onClick = {
                        /**Nur benötigt da bei combinedClickabled die die Funktion erwartet wird*/
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
                        Text(
                            text = device.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp)
                                .padding(top = 3.dp),
                            textAlign = TextAlign.Start,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis

                        )
                        Text(
                            text = group.name,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp)
                                .padding(top = 3.dp),
                            textAlign = TextAlign.Start,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )


                }
                val trueFalseDevices = listOf("F", "S", "T", "V")

                var deviceValue: String
                if(device.messwertTyp in trueFalseDevices ) {
                    if(device.value == "0") {
                        deviceValue = "open"
                    } else {
                        deviceValue = "closed"
                    }
                } else {
                    deviceValue = device.value
                }
                Text(
                    text = when (device.messwertTyp) {
                         "TLFH" -> "${device.value} °C"
                          "TEMP" -> "${device.value} °C"
                        else -> {
                            deviceValue
                        }
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
         * Zeigt ein Dialog an zum entfernen des Favoriten Status
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
                        groupViewModel.updateFavorite(group.id, device)

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
     * Erstellt eine Karte für Geräte, mit denen interagiert werden kann,
     * diese Werden innerhalb der Gruppenansicht angezeigt als auch in den Favoriten.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ActionDeviceCard(groupId: Int?, device: ParsedDevices, viewModel: FavoriteViewModel = viewModel(), groupViewModel: GroupViewModel = viewModel(), serverConfigViewModel: ServerConfigViewModel = viewModel()) {
        val context = LocalContext.current
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        var showDialog by remember { mutableStateOf(false) }
        var showPopup by remember { mutableStateOf(false) }


        Box(
            modifier = Modifier
                .size(105.dp)
                .padding(5.dp)
                .combinedClickable(
                    onClick = {
                        Log.d("Firebase Action Card", "Clicked $device $groupId")
                        if(!device.status)
                        {
                            showPopup = true
                        }
                        if(groupId != null)
                        {
                            groupViewModel.updateGroup(groupId, device)
                        }
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
                    containerColor = if (device.status) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
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
                        text = if(device.name != "") device.name else "empty name" ,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 3.dp),
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if(device.status) "On" else "Off",
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
        if (showPopup) {
            PopUp(device){ showPopup = false}
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
                    if (device.favorite) {
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
                    if (device.favorite) Text("Favorit entfernen") else Text("Favorit hinzufügen")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            groupViewModel.updateFavorite(groupId, device)
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

    /**
     * Pop welches beim Betätigen eines Gerätes angezeigt wird, um zu signalisieren, dass das Gerät aktiviert worden ist
     */
    @Composable
    fun PopUp(device: ParsedDevices, onDismiss: () -> Unit) {
       val hapticFeedback = LocalHapticFeedback.current

       val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success_animation_green))
       val progress by animateLottieCompositionAsState(composition, restartOnPlay = false)

       LaunchedEffect(progress) {
           if (progress == 1f) {
               onDismiss()
               hapticFeedback.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
           }
       }

       Dialog(onDismissRequest = { onDismiss() },
           properties = DialogProperties(usePlatformDefaultWidth = true, ) // Kein Abdunkeln
       ) {
           Box(
               modifier = Modifier
                   .fillMaxSize()
                   .padding(bottom = 30.dp),
               contentAlignment = Alignment.BottomStart

           ) {
               Box(
                   modifier = Modifier
                       .fillMaxWidth()
                       .height(150.dp)
                       .background(Color.White, shape = RoundedCornerShape(20.dp))
                       .padding(16.dp)
               ) {
                   Column(
                       modifier = Modifier.fillMaxSize(),
                       verticalArrangement = Arrangement.Center,
                       horizontalAlignment = Alignment.CenterHorizontally
                   ) {
                       LottieAnimation(
                           composition = composition,
                           progress = { progress },
                           modifier = Modifier.size(80.dp)
                       )
                       Text(
                           "${device.name} wurde angeschaltet",
                           color = Color.Black,
                           maxLines = 1,
                           overflow = TextOverflow.Ellipsis)
                   }

               }

           }
       }


   }


}
