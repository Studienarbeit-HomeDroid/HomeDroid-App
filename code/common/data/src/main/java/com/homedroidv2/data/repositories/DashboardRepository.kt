package com.homedroidv2.data.repositories

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.homedroidv2.data.interfaces.IDashboardRepository
import com.homedroidv2.data.model.DashboardValues
import com.homedroidv2.data.model.HeizungValues
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class DashboardRepository @Inject constructor(
    private val database: FirebaseDatabase
) : IDashboardRepository {

    private val dashboardRef: DatabaseReference = database.getReference("dashboard")


    override fun saveDashboardValuesList(dashboardList: List<DashboardValues>) {
        dashboardList.forEach { item ->
            try {
                dashboardRef.child(item.id).setValue(item)
                    .addOnSuccessListener {
                        Log.d("FirebaseUpdate", "DashboardValue ${item.id} gespeichert.")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseUpdate", "Fehler beim Speichern von DashboardValue ${item.id}: ${e.message}", e)
                    }
            } catch (e: Exception) {
                Log.e("FirebaseUpdate", "Exception bei DashboardValue ${item.id}: ${e.message}", e)
            }
        }
    }

     override fun saveHeizungValuesList(heizungList: List<HeizungValues>) {
        val heizungRef = database.getReference("heizung")

        heizungList.forEach { item ->
            try {
                heizungRef.child(item.id).setValue(item)
                    .addOnSuccessListener {
                        Log.d("FirebaseUpdate", "HeizungValue ${item.id} gespeichert.")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseUpdate", "Fehler beim Speichern von HeizungValue ${item.id}: ${e.message}", e)
                    }
            } catch (e: Exception) {
                Log.e("FirebaseUpdate", "Exception bei HeizungValue ${item.id}: ${e.message}", e)
            }
        }
    }

    override suspend fun getDashboardFlow(): Flow<List<DashboardValues>> = callbackFlow {
        Log.d("DashboardRepository", "Get Dashboard data")

        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("DashboardRepository", "$dataSnapshot")

                val dashboardList = dataSnapshot.children.mapNotNull { snapshot ->
                    Log.d("DashboardRepository", "Received data: $snapshot")
                    snapshot.getValue(DashboardValues::class.java)
                }
                trySend(dashboardList).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Fehler beim Abruf: $error")
                trySend(emptyList()).isSuccess
            }
        }

        dashboardRef.addValueEventListener(listener)
        awaitClose {
            dashboardRef.removeEventListener(listener)
        }

    }

    override suspend fun getHeizungFlow(): Flow<List<HeizungValues>> = callbackFlow {

        val heizungRef: DatabaseReference = database.getReference("heizung")

        Log.d("DashboardRepository", "Get Heizung data")

        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("DashboardRepository", "$dataSnapshot")

                val heizungList = dataSnapshot.children.mapNotNull { snapshot ->
                    Log.d("DashboardRepository", "Received heizung data: $snapshot")
                    snapshot.getValue(HeizungValues::class.java)
                }
                trySend(heizungList).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Fehler beim Abruf Heizung: $error")
                trySend(emptyList()).isSuccess
            }
        }

        heizungRef.addValueEventListener(listener)
        awaitClose {
            heizungRef.removeEventListener(listener)
        }
    }

    override suspend fun updateHeizungValue(id: String, newValue: String) {
        val heizungRef: DatabaseReference = database.getReference("heizung").child(id).child("values")

        try {
            heizungRef.setValue(newValue)
                .addOnSuccessListener {
                    Log.d("Firebase", "Heizungswert $id erfolgreich aktualisiert.")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Fehler beim Aktualisieren: ${e.message}", e)
                }
        } catch (e: Exception) {
            Log.e("Firebase", "Exception beim Setzen des Werts: ${e.message}", e)
        }
    }

    override suspend fun updateSolarDatas(newTagesValue: String, newGesamtValue:String)
    {
        val newValues = listOf(newTagesValue, newGesamtValue)

        try {
            Log.d("FirebaseUpdate", "Update SolarValues: $newValues")
            dashboardRef.child("5").child("values").setValue(newValues)
                .addOnSuccessListener {
                    Log.d("FirebaseUpdate", "Update erfolgreich!")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseUpdate", "Fehler beim Update: ${e.message}", e)
                }
        } catch (e: Exception) {
            Log.e("FirebaseUpdate", "Exception beim Schreiben: ${e.message}", e)
        }
    }

    override suspend fun updateStromDatas(newBezug: String, newZaehler:String)
    {
        val newValues = listOf(newBezug, newZaehler)

        try {
            Log.d("FirebaseUpdate", "Update SolarValues: $newValues")
            dashboardRef.child("3").child("values").setValue(newValues)
                .addOnSuccessListener {
                    Log.d("FirebaseUpdate", "Update erfolgreich!")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseUpdate", "Fehler beim Update: ${e.message}", e)
                }
        } catch (e: Exception) {
            Log.e("FirebaseUpdate", "Exception beim Schreiben: ${e.message}", e)
        }
    }

    override suspend fun updateSZaehlerDatas(newBezug: String, newLieferung:String)
    {
        val newValues = listOf(newBezug, newLieferung)

        try {
            Log.d("FirebaseUpdate", "Update SolarValues: $newValues")
            dashboardRef.child("4").child("values").setValue(newValues)
                .addOnSuccessListener {
                    Log.d("FirebaseUpdate", "Update erfolgreich!")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseUpdate", "Fehler beim Update: ${e.message}", e)
                }
        } catch (e: Exception) {
            Log.e("FirebaseUpdate", "Exception beim Schreiben: ${e.message}", e)
        }
    }

    override suspend fun updateWindowDatas(newUnverriegeltValue: String)
    {
        try {
            Log.d("FirebaseUpdateWINDOWSDATA", "ANZAHL AN OFFENEN FENSTER: $newUnverriegeltValue")
            dashboardRef.child("1").child("values").child("0").setValue(newUnverriegeltValue)
                .addOnSuccessListener {
                    Log.d("FirebaseUpdate", "Update erfolgreich!")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseUpdate", "Fehler beim Update: ${e.message}", e)
                }
        } catch (e: Exception) {
            Log.e("FirebaseUpdate", "Exception beim Schreiben: ${e.message}", e)
        }
    }

    override suspend fun updateOpenDoorDatas(newOffeneDoorValue: String)
    {
        try {
            Log.d("FirebaseUpdate", "Update SolarValues: $newOffeneDoorValue")
            dashboardRef.child("2").child("values").child("0").setValue(newOffeneDoorValue)
                .addOnSuccessListener {
                    Log.d("FirebaseUpdate", "Update erfolgreich!")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseUpdate", "Fehler beim Update: ${e.message}", e)
                }
        } catch (e: Exception) {
            Log.e("FirebaseUpdate", "Exception beim Schreiben: ${e.message}", e)
        }
    }

    override suspend fun updateClosedDoorDatas(newGeschlosseneDoorValue: String)
    {
        try {
            Log.d("FirebaseUpdate", "Update SolarValues: $newGeschlosseneDoorValue")
            dashboardRef.child("2").child("values").child("1").setValue(newGeschlosseneDoorValue)
                .addOnSuccessListener {
                    Log.d("FirebaseUpdate", "Update erfolgreich!")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseUpdate", "Fehler beim Update: ${e.message}", e)
                }
        } catch (e: Exception) {
            Log.e("FirebaseUpdate", "Exception beim Schreiben: ${e.message}", e)
        }
    }
}