package com.homedroid.data.repositories

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.homedroid.data.interfaces.IDashboardRepository
import com.homedroid.data.model.DashboardValues
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class DashboardRepository @Inject constructor(
    private val database: FirebaseDatabase
) : IDashboardRepository {

    private val favoritesRef: DatabaseReference = database.getReference("dashboard")

    val dashboardList = listOf(
        DashboardValues(
            id = "1",
            title = "Haustür",
            subtitle = listOf("status"),
            values = listOf("offen")
        ),
        DashboardValues(
            id = "2",
            title = "Fenster",
            subtitle = listOf("unverriegelt"),
            values = listOf("2")
        ),
        DashboardValues(
            id = "3",
            title = "Türen",
            subtitle = listOf("unverriegelt", "offen"),
            values = listOf("2", "1")
        ),
        DashboardValues(
            id = "4",
            title = "Strom",
            subtitle = listOf("bezug", "liefeung"),
            values = listOf("10", "12"),
            unit = "kwh"
        ),
        DashboardValues(
            id = "5",
            title = "Zähler",
            subtitle = listOf("bezug", "lieferung"),
            values = listOf("10", "12"),
            unit = "kwh"
        ),
        DashboardValues(
            id = "6",
            title = "Solar",
            subtitle = listOf("tages", "gesamt"),
            values = listOf("10", "12"),
            unit = "kwh"
        )
    )

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

        favoritesRef.addValueEventListener(listener)
        awaitClose {
            favoritesRef.removeEventListener(listener)
        }

    }
}