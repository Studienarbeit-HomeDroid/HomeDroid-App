package com.example.places.components

import BottomSheetViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.car.app.connection.CarConnection
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.places.data.model.Group
import com.example.places.ui.theme.HomeDroidTheme

class GroupComponent() {

    @RequiresApi(Build.VERSION_CODES.Q)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Group(group: Group) {

        val openButtomSheet: BottomSheetViewModel = BottomSheetViewModel()
        val modalButtomSheetComponent: ModalButtomSheetComponent = ModalButtomSheetComponent()
        rememberModalBottomSheetState()

        HomeDroidTheme {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(5.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .size(width = 70.dp, height = 70.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            openButtomSheet.toggleBottomSheet()
                        },
                    elevation = CardDefaults.elevatedCardElevation()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Place,
                            contentDescription = "Favorite Icon",
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Text(
                    text = group.name,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .widthIn(max = 70.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (openButtomSheet.getBottomSheetValue()) {
                modalButtomSheetComponent.ModalButtomSheet(openButtomSheet, group)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun GroupList(carConnectionType: Int, groups: List<Group>) {
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
            items(groups) { group ->
                Group(group)
            }
        }
    }

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