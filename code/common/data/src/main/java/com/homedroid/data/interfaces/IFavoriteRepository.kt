package com.homedroid.data.interfaces

import com.homedroid.data.model.Device

interface IFavoriteRepository {
    suspend fun getFavorites(): List<Device>
    suspend fun addFavorite(device: Device)
    suspend fun removeFavorite(device: Device)

}