package com.homedroid.data.interfaces

import com.homedroid.data.model.Device
import kotlinx.coroutines.flow.Flow

interface IFavoriteRepository {
    suspend fun getFavoritesFlow(): Flow<List<Device>> // Neue Methode
    suspend fun addFavorite(device: Device)
    suspend fun removeFavorite(device: Device)
    suspend fun updateFavorites(groupId: Int, device: Device.ActionDevice)

}