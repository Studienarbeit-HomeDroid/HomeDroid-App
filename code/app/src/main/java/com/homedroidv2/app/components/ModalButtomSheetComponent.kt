package com.homedroidv2.app.components

import com.homedroidv2.app.viewmodel.BottomSheetViewModel
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.homedroidv2.app.viewmodel.FavoriteViewModel
import com.homedroidv2.app.viewmodel.GroupViewModel
import com.homedroidv2.data.model.ParsedDevices
import com.homedroidv2.data.model.ParsedGroup
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ModalButtomSheetComponent {

    /**
     * Erzeugt das BottomSheet für die Gruppenansicht (Die Komponente die erscheint wenn auf eine Gruppe gedrückt wird).
     *
     * Die ViewModels verwalten den Status der Sheet und die Daten für die Anzeige.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ModalButtomSheet(openButtomSheet: BottomSheetViewModel, group: ParsedGroup) {
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
                /**
                 * Abhängig vom Gerätetypen werden die Geräte zu unterschiedlichen Gruppen zugeordnet
                 */
                if (group.devices.filter { it.messwertTyp == "Temp" || it.messwertTyp == "TLFH" }.isNotEmpty()) {
                    Text(
                        text = "Temperatur",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    LazyColumn {
                        items(group.devices.filter { it.messwertTyp == "Temp"  || it.messwertTyp == "TLFH"}) { device ->
                            DeviceRow(group, device)
                        }
                    }
                }

                if (group.devices.filter { it.messwertTyp == "F" }.isNotEmpty()) {
                    Text(
                        text = "Fensterstatus",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    LazyColumn {
                        items(group.devices.filter { it.messwertTyp == "F" }) { device ->
                            DeviceRow(group, device)
                        }
                    }
                }

                if (group.devices.filter { it.messwertTyp  == "R" }.isNotEmpty()) {
                    Text(
                        text = "Rollladen",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    LazyColumn {
                        items(group.devices.filter { it.messwertTyp == "R" }) { device ->
                            DeviceRow(group, device)
                        }
                    }
                }

                if (group.devices.filter { it.messwertTyp == "T" || it.messwertTyp == "V" }.isNotEmpty()) {
                    Text(
                        text = "Türen",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    LazyColumn {
                        items(group.devices.filter { it.messwertTyp == "T" || it.messwertTyp == "V"}) { device ->
                            DeviceRow(group, device)
                        }
                    }
                }

                if (group.devices.filter { it.messwertTyp == "S" }.isNotEmpty()) {
                    Text(
                        text = "Status",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                        ActionDevicesRow(group.id,group.devices.filter { it.messwertTyp  == "S" })
                }


                /**
                 * Für alle deren Typ nicht den oberen entspricht werden hier angezeigt
                 */

                val knownTypes = listOf("Temp", "TLFH", "F", "R", "S", "T", "V")

                val otherDevices = group.devices.filter { it.messwertTyp !in knownTypes || it.messwertTyp.isEmpty() }

                if (otherDevices.isNotEmpty()) {
                    Text(
                        text = "Andere Geräte",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    LazyColumn {
                        items(otherDevices) { device ->
                            DeviceRow(group, device)
                        }
                    }
                }
            }
        }
    }

    /**
     * Erstellt die Spaltenansicht der Geräte
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun ActionDevicesRow(groupId: Int, items: List<ParsedDevices>) {
        val favoriteCardComponent = CardComponent()
        Log.i("DEVICES IN LIST", "Devices In List ${items.size}")

        Column {
            items.chunked(4).forEach { rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Log.i("DEVICES IN LIST", "Devices In List ${rowItems.size}")

                    items.forEach { item ->
                        favoriteCardComponent.ActionDeviceCard(groupId, item)
                    }

                }
            }
        }
    }

    /**
     * Erzeugt die einzelne Reihe innerhalb der Liste
     */
    @OptIn(DelicateCoroutinesApi::class)
    @Composable
    fun DeviceRow(group: ParsedGroup, device: ParsedDevices, viewModel: FavoriteViewModel = viewModel(), groupViewModel: GroupViewModel = viewModel()) {
        var swipeOffset by remember { mutableStateOf(0f) }
        var isFavorite by remember { mutableStateOf(false) }

        /**
         *  Punkt bis vohin die Karte nach rechts verschoben werden kann
         */
        val swipeThreshold = 60f

        /**
         * Maximale Bewegung nach rechts
         */
        val maxSwipe = 80f

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .padding(6.dp)
                .pointerInput(Unit) {
                    /**
                     * Berechnung zur Darstellung der Favoriten Icons oder nicht
                     */
                    detectHorizontalDragGestures { _, dragAmount ->
                        swipeOffset = (swipeOffset + dragAmount).coerceIn(-maxSwipe, 0f)
                    }
                }
        ) {
            val trueFalseDevices = listOf("F", "S", "T", "V")

            when (device.messwertTyp) {
                "TEMP", "TLFH" -> {
                    RowContent(
                        name = device.name,
                        value = device.value,
                        unit = "°C",
                        swipeOffset
                    )
                }
                else -> {
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
                    RowContent(
                        name = if(device.name != "") device.name else "empty name" ,
                        value = deviceValue,
                        unit = "",
                        swipeOffset
                    )
                }
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
                            groupViewModel.updateFavorite(group.id, device)
                            isFavorite = !isFavorite
                            GlobalScope.launch {
                                delay(1000)
                                swipeOffset = 0F
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (device.favorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (device.favorite) "Favorite" else "No Favorite",
                        tint = Color.Black,
                        modifier = Modifier.fillMaxWidth(0.6F)
                    )
                }
            }
        }
    }


    /**
     * Zeigt die Werte der Devices an
     */
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



