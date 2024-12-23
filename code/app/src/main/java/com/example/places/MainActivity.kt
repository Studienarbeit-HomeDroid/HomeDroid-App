package com.example.places

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.car.app.CarContext
import androidx.car.app.connection.CarConnection
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.places.ViewModels.FavoriteViewModel
import com.example.places.carappservice.screen.MainScreen
import com.example.places.components.DashboardComponent
import com.example.places.components.FavoriteComponents
import com.example.places.components.GroupComponent
import com.example.places.data.GroupRepository
import com.example.places.ui.theme.HomeDroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Komponenten
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
                    // Liste der Gruppen anzeigen
                    groupComponent.GroupList(
                        carConnectionType,
                        groups = GroupRepository().getGroupItems()
                    )

                    // Dashboard anzeigen
                    dashboardComponent.Dashboard()

                    // Favoriten anzeigen
                    favoriteComponent.Favorite()
                }
            }
        }
    }
}