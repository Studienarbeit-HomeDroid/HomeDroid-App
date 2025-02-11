/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.places.data.repositories

import android.util.Log
import com.example.places.data.interfaces.IGroupRepository
import com.example.places.data.model.Device
import com.example.places.data.model.Group
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await



class GroupRepository: IGroupRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val groupsRef: DatabaseReference = database.getReference("groups")
    private val userId = "user123"

    override suspend fun saveGroups(groups: List<Group>) {
        val dataToSave = groups.map { group ->
            mapOf(
                "id" to group.id,
                "name" to group.name,
                "iconUrl" to group.iconUrl,
                "devices" to group.devices.map { device ->
                    when (device) {
                        is Device.StatusDevice -> mapOf(
                            "type" to "StatusDevice",
                            "id" to device.id,
                            "name" to device.name,
                            "description" to device.description,
                            "value" to device.value,
                            "unit" to device.unit,
                            "group" to device.group
                        )
                        is Device.ActionDevice -> mapOf(
                            "type" to "ActionDevice",
                            "id" to device.id,
                            "name" to device.name,
                            "status" to device.status,
                            "group" to device.group
                        )
                        is Device.TemperatureDevice -> mapOf(
                            "type" to "TemperatureDevice",
                            "id" to device.id,
                            "name" to device.name,
                            "value" to device.value,
                            "group" to device.group
                        )
                    }
                }
            )
        }

        groupsRef.child(userId).setValue(dataToSave).await()
    }


    override suspend fun getGroupItems(): List<Group> {
        return try {
            val dataSnapshot = groupsRef.child(userId).get().await()
            if (dataSnapshot.exists()) {
                val groups = dataSnapshot.children.mapNotNull { snapshot ->
                    // Lade nur die Gruppe (ohne Geräte)
                    val groupId = snapshot.child("id").getValue(Long::class.java)
                    val groupName = snapshot.child("name").getValue(String::class.java)
                    val groupIconUrl = snapshot.child("iconUrl").getValue(String::class.java)

                    // Hier erstellen wir eine neue Instanz von Group ohne Devices
                    val group = groupName?.let { groupId?.let { it1 -> Group(id = it1.toInt(), name = groupName, iconUrl = groupIconUrl, devices = mutableListOf()) } }

                    // Lade die Geräte separat
                    if (group != null) {
                        snapshot.child("devices").children.mapNotNull { deviceSnapshot ->
                            val type = deviceSnapshot.child("type").getValue(String::class.java)

                            Log.d("Firebase", "Processing device with type: $type")

                            // Typenüberprüfung und Instanziierung
                            when (type) {
                                "StatusDevice" -> {
                                    val device = deviceSnapshot.getValue(Device.StatusDevice::class.java)
                                    Log.d("Firebase", "Parsed StatusDevice: ${device?.name}")
                                    device?.let { group.devices.add(it) }
                                }

                                "ActionDevice" -> {
                                    val device = deviceSnapshot.getValue(Device.ActionDevice::class.java)
                                    Log.d("Firebase", "Parsed ActionDevice: ${device?.name}")
                                    device?.let { group.devices.add(it) }
                                }

                                "TemperatureDevice" -> {
                                    val device = deviceSnapshot.getValue(Device.TemperatureDevice::class.java)
                                    Log.d("Firebase", "Parsed TemperatureDevice: ${device?.name}")
                                    device?.let { group.devices.add(it) }
                                }

                                else -> {
                                    Log.e("Firebase", "Unknown device type: $type")
                                }
                            }
                        }
                    }
                    group
                }
                return groups
            }else {
                Log.i("Firebase", "No groups found in the data snapshot.")
                return emptyList()
            }
        } catch (e: Exception) {
            Log.e("Firebase", "getGroupItems: " + e.toString())
            emptyList()
        }
    }


    override suspend fun addGroupToList(group: Group){
        val groups = getGroupItems()
        if (!groups.contains(group)) {
            Log.i("Group", group.toString())
            val updatedFavorites = groups.toMutableList().apply { add(group) }
            saveGroups(updatedFavorites)
        }    }

    fun deleteGroupTabel() {
        groupsRef.child(userId).removeValue()

   }
}