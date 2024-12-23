package com.example.places.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.places.data.FavoriteRepository
import com.example.places.data.model.Device
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Device>>(emptyList())
    val favorites: StateFlow<List<Device>> get() = _favorites

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _favorites.value = favoriteRepository.getFavorites()
        }
    }

    fun addFavorite(item: Device) {
        Log.i("Test", item.id)
        viewModelScope.launch {
            favoriteRepository.addFavorite(item)
            loadFavorites()
        }
    }

    fun removeFavorite(item: Device) {
        viewModelScope.launch {
            favoriteRepository.removeFavorite(item)
            loadFavorites() // Favoriten nach dem Entfernen neu laden
        }
    }

    fun isFavorite(item: Device): Boolean {
        return favorites.value.contains(item)
    }
}