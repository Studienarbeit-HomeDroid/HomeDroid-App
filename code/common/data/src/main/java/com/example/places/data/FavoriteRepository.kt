package com.example.places.data

import android.util.Log
import com.example.places.data.interfaces.IFavoriteRepository
import com.example.places.data.model.Device
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FavoriteRepository @Inject constructor() : IFavoriteRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val favoritesRef: DatabaseReference = database.getReference("favorites")
    private val userId = "user123"

    override suspend fun getFavorites(): List<Device> {
        return try {
            val dataSnapshot = favoritesRef.child(userId).get().await()

            if (dataSnapshot.exists()) {
                val favorites = dataSnapshot.children.mapNotNull { snapshot ->
                    val type = snapshot.child("type").getValue(String::class.java)
                    when (type) {
                        "StatusDevice" -> snapshot.getValue(Device.StatusDevice::class.java)
                        "ActionDevice" -> snapshot.getValue(Device.ActionDevice::class.java)
                        else -> null
                    }
                }
                return favorites
            } else {
                Log.i("Firebase", "Keine Favoriten gefunden.")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("Firebase", e.toString())
            emptyList()
        }
    }

    suspend fun saveFavorites(favorites: List<Device>) {
        val dataToSave = favorites.map {
            when (it) {
                is Device.StatusDevice -> {
                    mapOf(
                        "type" to "StatusDevice",
                        "id" to it.id,
                        "name" to it.name,
                        "description" to it.description,
                        "value" to it.value,
                        "unit" to it.unit,
                        "isFavorite" to it.isFavorite
                    )
                }
                is Device.ActionDevice -> {
                    mapOf(
                        "type" to "ActionDevice",
                        "id" to it.id,
                        "name" to it.name,
                        "status" to it.status,
                        "isFavorite" to it.isFavorite
                    )
                }
            }
        }
        favoritesRef.child(userId).setValue(dataToSave).await()
    }

    override suspend fun addFavorite(device: Device) {
        val favorites = getFavorites()
        Log.i("Anzahl", favorites.size.toString())
        if (!favorites.contains(device)) {
            val updatedFavorites = favorites.toMutableList().apply { add(device) }
            saveFavorites(updatedFavorites)
        }
    }

    override suspend fun removeFavorite(device: Device) {
        val favorites = getFavorites()
        val updatedFavorites = favorites.filterNot { it.id == device.id }
        saveFavorites(updatedFavorites)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object FavoriteRepositoryModule {

    @Provides
    fun provideFavoriteRepository(): IFavoriteRepository {
        return FavoriteRepository()
    }
}