package com.homedroid.data.repositories

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.homedroid.data.interfaces.IFavoriteRepository
import com.homedroid.data.model.Device
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FavoriteRepository @Inject constructor(
    private val database: FirebaseDatabase // Inject FirebaseDatabase
) : IFavoriteRepository {

    private var userId: String = "user123"
    private val favoritesRef: DatabaseReference = database.getReference("favorites")

    private fun deviceToMap(device: Device): Map<String, Any?> {
        return when (device) {
            is Device.StatusDevice -> mapOf(
                "type" to "StatusDevice",
                "id" to device.id,
                "name" to device.name,
                "description" to device.description,
                "value" to device.value,
                "unit" to device.unit,
                "group" to device.group,
                "groupid" to device.groupid,
                "type" to device.type,
            )
            is Device.ActionDevice -> mapOf(
                "type" to "ActionDevice",
                "id" to device.id,
                "name" to device.name,
                "status" to device.status,
                "group" to device.group,
                "groupid" to device.groupid,
                "type" to device.type,
            )
            is Device.TemperatureDevice -> mapOf(
                "type" to "TemperatureDevice",
                "id" to device.id,
                "name" to device.name,
                "value" to device.value,
                "group" to device.group,
                "groupid" to device.groupid,
                "type" to device.type,
            )
        }
    }

    // Echtzeit-Flow für Favoriten
     override  suspend fun getFavoritesFlow(): Flow<List<Device>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val favorites = dataSnapshot.children.mapNotNull { snapshot ->
                    val type = snapshot.child("type").getValue(String::class.java)
                    when (type) {
                        "StatusDevice" -> snapshot.getValue(Device.StatusDevice::class.java)
                        "ActionDevice" -> snapshot.getValue(Device.ActionDevice::class.java)
                        "TemperatureDevice" -> snapshot.getValue(Device.TemperatureDevice::class.java)
                        else -> {
                            Log.w("Firebase", "Unbekannter Typ: $type")
                            null
                        }
                    }
                }
                trySend(favorites).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Fehler beim Abruf: $error")
                trySend(emptyList()).isSuccess
            }
        }

        favoritesRef.child(userId).addValueEventListener(listener)
        awaitClose {
            favoritesRef.child(userId).removeEventListener(listener)
        }
    }

    // Hilfsfunktion für einmaligen Abruf der aktuellen Liste
    private suspend fun getCurrentFavorites(): List<Device> {
        return try {
            val dataSnapshot = favoritesRef.child(userId).get().await()
            dataSnapshot.children.mapNotNull { snapshot ->
                val type = snapshot.child("type").getValue(String::class.java)
                when (type) {
                    "StatusDevice" -> snapshot.getValue(Device.StatusDevice::class.java)
                    "ActionDevice" -> snapshot.getValue(Device.ActionDevice::class.java)
                    "TemperatureDevice" -> snapshot.getValue(Device.TemperatureDevice::class.java)
                    else -> null
                }
            }
        } catch (e: Exception) {
            Log.e("Firebase", "Fehler beim einmaligen Abruf: $e")
            emptyList()
        }
    }

    override suspend fun updateFavorites(groupId: Int, device: Device.ActionDevice) {
        Log.i("Firebase", "Updating device status: ${groupId}+${device}")
        val deviceRef = favoritesRef.child(userId).child(device.id)
        deviceRef.child("status").setValue(!device.status).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("Firebase", "Device status updated successfully.")
            } else {
                Log.e("Firebase", "Failed to update device status.", task.exception)
            }
        }
    }

    // Speichere Favoriten in Firebase
    suspend fun saveFavorites(favorites: List<Device>) {
        try {
            val dataToSave = favorites.associateBy({ it.id }, { deviceToMap(it) })
            favoritesRef.child(userId).setValue(dataToSave).await()
        } catch (e: Exception) {
            Log.e("Firebase", "Fehler beim Speichern der Favoriten: $e")
            throw e // Optional: Fehler weitergeben
        }
    }

    override suspend fun addFavorite(device: Device) {
        Log.i("Favorite", "Adding favorite: $device")
        val currentFavorites = getCurrentFavorites()
        if (currentFavorites.none { it.id == device.id }) {
            val updatedFavorites = currentFavorites + device
            saveFavorites(updatedFavorites)
        } else {
            Log.i("FavoriteRepository", "Device ${device.id} ist bereits ein Favorit.")
        }
    }

    override suspend fun removeFavorite(device: Device) {
        val currentFavorites = getCurrentFavorites()
        val updatedFavorites = currentFavorites.filterNot { it.id == device.id }
        saveFavorites(updatedFavorites)
    }
}

