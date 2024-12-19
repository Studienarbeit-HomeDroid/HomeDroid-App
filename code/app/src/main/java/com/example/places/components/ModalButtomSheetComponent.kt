package com.example.places.components

import BottomSheetViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.places.data.model.Device
import com.example.places.data.model.Group

class ModalButtomSheetComponent {

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
                    .padding(bottom = 5.dp)

            ) {
                androidx.compose.material3.Text(
                    text = group.name,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(top = 13.dp)
                        .padding(bottom = 8.dp)
                )


                androidx.compose.material3.Text(
                    text = "Geräte",
                    textAlign = TextAlign.Center,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                )

                LazyColumn {
                    items(group.devices) { devices ->
                        DeviceRow(devices)
                    }
                }

                androidx.compose.material3.Text(
                    text = "Status",
                    textAlign = TextAlign.Center,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                )

                ActionDevicesRow( group.devices)
            }
        }
    }


    @Composable
    fun ActionDevicesRow( items: List<Device>)
    {
      val  favoriteCardComponent: FavoriteCardComponent = FavoriteCardComponent()
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            items.chunked(4).forEach { rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowItems.forEach { item ->
                        if(item is Device.StatusDevice)
                        {
                            favoriteCardComponent.ActionCard( item.name, "ein", item.status, )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun DeviceRow(device: Device) {
        if (device is Device.PhysicalDevice) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)

            ) {
                Row(
                    modifier = Modifier.padding(vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Plus Icon",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )

                    androidx.compose.material3.Text(
                        text = device.name,
                        style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .padding(start = 10.dp)
                    )
                }

                androidx.compose.material3.Text(
                    text = "${device.value} ${device.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)

                )
            }
        }
    }


}



