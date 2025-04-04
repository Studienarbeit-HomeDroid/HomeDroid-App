package com.homedroid.app.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homedroid.app.components.DashboardComponent
import com.homedroid.app.components.FavoriteComponents
import com.homedroid.app.components.GroupComponent
import com.homedroid.app.ui.theme.HomeDroidTheme
import com.homedroid.data.model.Group
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main screen activity which serves as the entry point for the app's main UI.
 * It organizes the layout by including different components such as the GroupList, Dashboard, and Favorites.
 */

@AndroidEntryPoint
class MainScreen : ComponentActivity() {
    private val groupComponent = GroupComponent()
    private val dashboardComponent = DashboardComponent()
    private val favoriteComponent = FavoriteComponents()

    @SuppressLint("NotConstructor")
    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun MainScreen(carConnectionType: Int = 0, htmlIsLoaded: Boolean) {
        HomeDroidTheme {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(ScrollState(1000))
                    .padding(WindowInsets.systemBars.asPaddingValues()),
                color = MaterialTheme.colorScheme.primary
            ) {
                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                    groupComponent.GroupList(
                        carConnectionType,
                        htmlIsLoaded
                    )
                    dashboardComponent.Dashboard()
                    favoriteComponent.Favorite()

                }
            }
        }
    }
}