package com.homedroid.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SensorDoor
import androidx.compose.material.icons.outlined.SensorDoor
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homedroid.app.ui.theme.HomeDroidTheme
import com.homedroid.app.viewmodel.DashboardViewModel

class DashboardComponent {

    /**
     * Displays the Dashboard Card, which displays the important information's of the website
     */
    @Composable
    fun Dashboard( viewModel: DashboardViewModel = viewModel()) {
        val dashboardData = viewModel.dashboardData
        HomeDroidTheme {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(top = 13.dp)
                    .padding(bottom = 8.dp)
            )
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(275.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {

                Column {
                    /**
                     * Displays the last update time of the Dashboard data's
                     */
                    Text(
                        text = "1.10.24 | 16:12:22",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 10.dp)
                            .padding(top = 5.dp),
                        textAlign = TextAlign.End
                    )
                    dashboardData.chunked(3).forEach { data ->

                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 10.dp)
                        ) {
                            data.forEach { data ->
                                Box(
                                    Modifier.size(width = 110.dp, height = 110.dp)
                                )
                                {
                                    Column {
                                        BoxHeaderText(data.title)
                                        BoxDescriptionText(data.subtitle.get(0))
                                        if (data.values.isNotEmpty()) {
                                            if(data.id.equals("1"))
                                            {
                                                BoxImageValue(data.values.get(0))
                                            }
                                            else
                                            {
                                                BoxValueText(data.values.get(0), data.unit)
                                            }
                                        }
                                        if (data.subtitle.size == 2) {
                                            BoxDescriptionText(data.subtitle.get(1))
                                            BoxValueText(data.values.get(1), data.unit)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun BoxHeaderText(text: String) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center

            )


    }

    @Composable
    fun BoxDescriptionText(description: String) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center

        )
    }

    @Composable
    fun BoxImageValue(value: String)
    {
        val icons: ImageVector = if (value.equals("offen")) Icons.Outlined.SensorDoor  else Icons.Filled.SensorDoor
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(100.dp)
                .background(Color.Transparent)
        ) {
                Icon(
                    imageVector = icons,
                    contentDescription = "Favorite Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.TopCenter)
                )
        }
    }

    @Composable
    fun BoxValueText(value: String, unit: String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = unit,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Thin
                ),
                modifier = Modifier.padding(start = 4.dp), // Abstand zwischen den Texten
                textAlign = TextAlign.Start
            )
        }
    }
}