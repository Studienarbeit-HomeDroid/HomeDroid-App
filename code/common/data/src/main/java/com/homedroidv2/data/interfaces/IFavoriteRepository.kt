package com.homedroidv2.data.interfaces

import com.homedroidv2.data.model.Device
import kotlinx.coroutines.flow.Flow

interface IFavoriteRepository {
    suspend fun getFavoritesFlow(): Flow<List<Device>> // Neue Methode
    suspend fun addFavorite(device: Device)
    suspend fun removeFavorite(device: Device)
    suspend fun updateFavorites(groupId: Int, device: Device.ActionDevice)

}