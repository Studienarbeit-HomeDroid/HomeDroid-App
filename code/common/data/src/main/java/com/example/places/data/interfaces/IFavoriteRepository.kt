package com.example.places.data.interfaces

import com.example.places.data.model.Device

interface IFavoriteRepository {
    suspend fun getFavorites(): List<Device>
    suspend fun addFavorite(device: Device)
    suspend fun removeFavorite(device: Device)

}