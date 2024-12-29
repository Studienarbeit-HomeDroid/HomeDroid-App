package com.example.places.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.activity.ComponentActivity
import com.example.places.components.DashboardComponent
import com.example.places.components.FavoriteComponents
import com.example.places.components.GroupComponent
import com.example.places.data.repositories.GroupRepository
import com.example.places.ui.theme.HomeDroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainScreen : ComponentActivity() {
    private val groupComponent = GroupComponent()
    private val dashboardComponent = DashboardComponent()
    private val favoriteComponent = FavoriteComponents()

    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun MainContent(carConnectionType: Int = 0) {
        HomeDroidTheme {

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(ScrollState(1000)),
                color = MaterialTheme.colorScheme.primary
            ) {
                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                    groupComponent.GroupList(
                        carConnectionType,
                        groups = GroupRepository().getGroupItems()
                    )
                    dashboardComponent.Dashboard()
                    favoriteComponent.Favorite()
                }
            }
        }
    }
}