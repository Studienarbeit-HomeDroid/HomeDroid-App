package com.example.places.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.places.carappservice.screen.FavoriteScreen

class FavoriteComponents
{
    @Composable
    fun Favorite(){
        Text(
            text = "Favorites",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(top = 13.dp)
                .padding(bottom = 8.dp)
        )
    }
}