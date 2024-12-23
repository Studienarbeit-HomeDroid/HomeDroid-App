package com.example.places.data

import android.util.Log
import com.example.places.data.model.Device
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FavoriteRepository @Inject constructor() {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val favoritesRef: DatabaseReference = database.getReference("favorites")
    private val userId = "user123"

    // Abrufen der Favoriten aus der Realtime Database
    suspend fun getFavorites(): List<Device> {
        return try {
            val dataSnapshot = favoritesRef.child(userId).get().await()

            if (dataSnapshot.exists()) {
                val favorites = dataSnapshot.children.mapNotNull { snapshot ->
                    // Hole die "type" Eigenschaft aus dem Snapshot
                    val type = snapshot.child("type").getValue(String::class.java)

                    // Je nach Typ, instanziiere die richtige Klasse
                    when (type) {
                        "PhysicalDevice" -> snapshot.getValue(Device.PhysicalDevice::class.java)
                        "ActionDevice" -> snapshot.getValue(Device.ActionDevice::class.java)
                        else -> null // Falls der Typ nicht bekannt ist, überspringen
                    }
                }
                return favorites
            } else {
                Log.i("Firebase", "Keine Favoriten gefunden.")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("Firebase", e.toString(), )
            emptyList()
        }
    }

    // Speichern der Favoriten in der Realtime Database
    suspend fun saveFavorites(favorites: List<Device>) {
        // Verwende eine Liste von Maps, da Firebase Daten nur als Map speichern kann
        val dataToSave = favorites.map {
            when (it) {
                is Device.PhysicalDevice -> {
                    mapOf(
                        "type" to "PhysicalDevice",
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

    // Favoriten hinzufügen
    suspend fun addFavorite(device: Device) {
        val favorites = getFavorites()
        Log.i("Anzahl", favorites.size.toString())
        if (!favorites.contains(device)) {
            val updatedFavorites = favorites.toMutableList().apply { add(device) }
            saveFavorites(updatedFavorites)
        }
    }

    // Ein bestimmtes Gerät entfernen
    suspend fun removeFavorite(device: Device) {
        val favorites = getFavorites()
        val updatedFavorites = favorites.filterNot { it.id == device.id }
        saveFavorites(updatedFavorites)
    }



}