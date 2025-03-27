package com.homedroidv2.app.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homedroidv2.app.viewmodel.FavoriteViewModel
import com.homedroidv2.app.viewmodel.GroupViewModel


class FavoriteComponents{

    private val cardComponent: CardComponent = CardComponent()
    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun Favorite() {
        Text(
            text = "Favorites",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(top = 13.dp)
                .padding(bottom = 8.dp)
        )
        ListOfFavorites()
    }

    /**
     * ListOfFavorites is a composable function that displays a grid of favorite devices,
     * organized in rows with a specified number of items per row.
     * Dynamically renders a list of favorite devices retrieved from the ViewModel's `favorites` state.
     *
     * @param viewModel The ViewModel responsible for managing favorite devices. Defaults to the current `FavoriteViewModel`.
     *
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun ListOfFavorites(viewModel: FavoriteViewModel = viewModel(), groupViewModel: GroupViewModel = viewModel()) {
        val items by groupViewModel.groups.collectAsState()
        val itemsPerRow = 3
        Log.i("FAVORITES", "$items")

        Column(
            modifier = Modifier.padding(8.dp)
        ) {

            items.forEach{ group ->
                group.devices.filter { it.favorite }.chunked(itemsPerRow).forEach { rowItems ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowItems.forEach {
                            Log.i("FAVORITES IN ROW", "$it")
                        when (it.messwertTyp) {
                            "S" -> cardComponent.ActionDeviceCard(group.id, it)
                            else -> cardComponent.TempAndStatusDeviceCard(group, it)
                        }
                        }
                    }
                }
            }
        }
    }
}