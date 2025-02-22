package com.homedroid.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homedroid.data.interfaces.IFavoriteRepository
import com.homedroid.data.model.Device
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepository: IFavoriteRepository
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Device>>(emptyList())
    val favorites: StateFlow<List<Device>> get() = _favorites

    init {
        loadFavorites()
    }

     fun loadFavorites() {
        viewModelScope.launch {
            _favorites.value = favoriteRepository.getFavorites()
        }
    }

    fun addFavorite(item: Device) {
        viewModelScope.launch {
            favoriteRepository.addFavorite(item)
            loadFavorites()
        }
    }

    fun removeFavorite(item: Device) {
        viewModelScope.launch {
            favoriteRepository.removeFavorite(item)
            loadFavorites()
        }
    }

    fun isFavorite(item: Device): Boolean {
        return favorites.value.contains(item)
    }
}