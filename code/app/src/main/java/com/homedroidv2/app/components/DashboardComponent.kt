package com.homedroidv2.app.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homedroidv2.app.ui.theme.HomeDroidTheme
import com.homedroidv2.app.viewmodel.DashboardViewModel

class DashboardComponent {

    /**
     * Composable welches zum Erzeugen der Dashboard Benutzeroberfläche verwendet wird
     * Dabei wird ein Tab Componten erzeugt die je nach Tabindex die Dashboarddaten oder die Heinzungsdaten anzeigt
     */
    @Composable
    fun Dashboard() {
        var selectedTabIndex by remember { mutableStateOf(0) }

        HomeDroidTheme {
            Column {
                val tabTitles = listOf("Dashboard", "Heizung")

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    indicator = {}, // kein Unterstrich
                    divider = {}
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        val isSelected = selectedTabIndex == index
                        Tab(
                            selected = isSelected,
                            onClick = { selectedTabIndex = index },
                            selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ) {
                            Text(
                                text = title,
                                modifier = Modifier
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                                    .background(
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        else
                                            Color.Transparent,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(vertical = 6.dp, horizontal = 12.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
                when (selectedTabIndex) {
                    0 -> DashboardView()
                    1 -> HeizungView()
                }
            }
        }
    }

    /**
     * Composable welches die Dashbaorddaten anzeigt. Die Daten werden vom DashboardViewModel abgerufen zur Trennung
     * von UI und Logik
     */
    @Composable
    fun DashboardView(viewModel: DashboardViewModel = viewModel()){
        val dashboardData = viewModel.dashboardData.collectAsState()
        Log.d("DashboardComponent", "dashboardData: $dashboardData")
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(275.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {

            Column{
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
                dashboardData.value?.chunked(3)?.forEach { data ->

                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 10.dp)
                    ) {
                        data.forEach { data ->
                            Box(
                                Modifier.size(width = 110.dp, height = 110.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                ) {
                                    BoxHeaderText(data.title)
                                    BoxDescriptionText(data.subtitle.get(0))
                                    if (data.values.isNotEmpty()) {
                                        if (data.id == "1") {
                                            BoxImageValue(data.values[0])
                                        } else {
                                            BoxValueText(data.values[0], data.unit)
                                        }
                                    }
                                    if (data.subtitle.size == 2) {
                                        BoxDescriptionText(data.subtitle[1])
                                        BoxValueText(data.values[1], data.unit)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Composable welches die Heizungsdaten anzeigt. Die Daten werden vom DashboardViewModel abgerufen zur Trennung
     * von UI und Logik
     */
    @Composable
    fun HeizungView(viewModel: DashboardViewModel = viewModel()){
        val heizungData = viewModel.heizungData.collectAsState()
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(275.dp),
        colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                heizungData.value.forEach {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${it.values} ${it.unit}",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }

    /**
     * Hilfs Composables zur Strukturierung des Dashboards
     */
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