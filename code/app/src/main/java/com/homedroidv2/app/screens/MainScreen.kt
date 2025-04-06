package com.homedroidv2.app.screens

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homedroidv2.app.components.DashboardComponent
import com.homedroidv2.app.components.FavoriteComponents
import com.homedroidv2.app.components.GroupComponent
import com.homedroidv2.app.ui.theme.HomeDroidTheme
import com.homedroidv2.app.viewmodel.ServerConfigViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * MainScreen Activity verwaltet die Hauptaktivitäten die in der App angezeigt werden
 * Ebenfalls wird auf dieser Ebene der Anwendung das Neuladen der Daten initiiert
 */

@AndroidEntryPoint
class MainScreen : ComponentActivity() {
    private val groupComponent = GroupComponent()
    private val dashboardComponent = DashboardComponent()
    private val favoriteComponent = FavoriteComponents()

    @OptIn(ExperimentalMaterialApi::class)
    @SuppressLint("NotConstructor")
    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun MainScreen(context: Context, carConnectionType: Int = 0, htmlIsLoaded: Boolean, serverConfigViewModel: ServerConfigViewModel = viewModel()) {
        val scope = rememberCoroutineScope()
        val isRefreshing = remember { mutableStateOf(false) }

        /**
         * Startet das neu Laden der Anwendung
         */
        val refreshState = rememberPullRefreshState(
            refreshing = isRefreshing.value,
            onRefresh = {
                isRefreshing.value = true
                scope.launch {
                    serverConfigViewModel.fetchAllDatas(context)
                    isRefreshing.value = false
                }
            }
        )

        HomeDroidTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(refreshState)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(ScrollState(1000))
                        .padding(WindowInsets.systemBars.asPaddingValues()),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    /**
                     * Stellt die einzelnen Komponenten der App dar
                     */
                    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                        groupComponent.GroupList(
                            carConnectionType,
                            htmlIsLoaded
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        dashboardComponent.Dashboard()
                        favoriteComponent.Favorite()
                    }
                }

                /**
                 * Komponente die die Aktualisierungsanzeige darstellt.
                 * Wird durch nach unten zeihen auf dem Bildschirm ausgelöst
                 */
                PullRefreshIndicator(
                    refreshing = isRefreshing.value,
                    state = refreshState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                )
            }
        }
    }
}