package com.homedroid.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homedroid.data.interfaces.IFavoriteRepository
import com.homedroid.data.model.Device
import com.homedroid.data.repositories.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepository: IFavoriteRepository // Interface statt konkrete Klasse für bessere Abstraktion
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Device>>(emptyList())
    val favorites: StateFlow<List<Device>> = _favorites.asStateFlow() // Sicherer, unveränderlicher Zugriff

    init {
        // Lade und beobachte die Favoriten beim Start
        viewModelScope.launch {
            favoriteRepository.getFavoritesFlow().collect { favoritesList ->
                _favorites.value = favoritesList // Aktualisiere den StateFlow bei Änderungen
            }
        }
    }

    fun addFavorite(item: Device) {
        viewModelScope.launch {
            favoriteRepository.addFavorite(item)
            // Kein explizites loadFavorites() nötig, da das Flow automatisch aktualisiert
        }
    }

    fun removeFavorite(item: Device) {
        viewModelScope.launch {
            favoriteRepository.removeFavorite(item)
            // Kein explizites loadFavorites() nötig, da das Flow automatisch aktualisiert
        }
    }

    fun isFavorite(item: Device): Boolean {
        return _favorites.value.any { it.id == item.id } // Vergleich über ID statt contains
    }
}