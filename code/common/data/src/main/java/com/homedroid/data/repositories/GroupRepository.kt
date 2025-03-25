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

package com.homedroid.data.repositories

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.homedroid.data.interfaces.IGroupRepository
import com.homedroid.data.model.Device
import com.homedroid.data.model.Group
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class GroupRepository @Inject constructor(
    private val database: FirebaseDatabase
): IGroupRepository {

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
                            "group" to device.group,
                            "groupid" to device.groupid,
                            "type"  to device.type
                        )
                        is Device.ActionDevice -> mapOf(
                            "type" to "ActionDevice",
                            "id" to device.id,
                            "name" to device.name,
                            "status" to device.status,
                            "group" to device.group,
                            "groupid" to device.groupid,
                            "type"  to device.type
                        )
                        is Device.TemperatureDevice -> mapOf(
                            "type" to "TemperatureDevice",
                            "id" to device.id,
                            "name" to device.name,
                            "value" to device.value,
                            "group" to device.group,
                            "groupid" to device.groupid,
                            "type"  to device.type
                        )
                    }
                }
            )
        }

        groupsRef.child(userId).setValue(dataToSave).await()
    }

    override suspend fun getGroupItemsFlow(): Flow<List<Group>> = callbackFlow {
        Log.i("Firebase", "getGroupItemsFlow")
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                dataSnapshot.children.mapNotNull { result ->
                   val  groups = result.children.mapNotNull { snapshot ->

                        val groupName = snapshot.child("name").getValue(String::class.java)
                        Log.i("Firebase", "Group found: $groupName")
                        val iconUrl = snapshot.child("iconUrl").getValue(String::class.java)
                        val groupId = snapshot.child("id").getValue(Int::class.java)

                        val devices =
                            snapshot.child("devices").children.mapNotNull { deviceSnapshot ->
                                val deviceName =
                                    deviceSnapshot.child("name").getValue(String::class.java)
                                val deviceId = deviceSnapshot.child("id").getValue(String::class.java)
                                val deviceType =
                                    deviceSnapshot.child("type").getValue(String::class.java)
                                val deviceValue =
                                    deviceSnapshot.child("value").getValue(String::class.java)
                                val deviceStatus =
                                    deviceSnapshot.child("status").getValue(Boolean::class.java)
                                        ?: false

                                when (deviceType) {
                                    "ActionDevice" -> Device.ActionDevice(
                                        deviceId.toString(),
                                        deviceName ?: "",
                                        deviceStatus,
                                        groupName ?: "",
                                        groupId.toString(),
                                        "ActionDevice"
                                    )

                                    "StatusDevice" -> deviceValue?.let {
                                        Device.StatusDevice(
                                            deviceId.toString(),
                                            deviceName ?: "",
                                            "",
                                            deviceValue,
                                            "",
                                            groupName ?: "",
                                            groupId.toString(),
                                            "StatusDevice"
                                        )
                                    }

                                    "TemperatureDevice" -> deviceValue?.let {
                                        Device.TemperatureDevice(
                                            deviceId.toString(),
                                            deviceName ?: "",
                                            deviceValue,
                                            groupName ?: "",
                                            groupId.toString(),
                                            "TemperatureDevice"
                                        )
                                    }

                                    else -> null
                                }
                            }

                        if (groupName != null && groupId != null) {
                            Log.i("Firebase", "Group found: $groupName")
                            Group(
                                id = groupId,
                                name = groupName,
                                iconUrl = iconUrl ?: "",
                                devices = devices.toMutableList()
                            )
                        } else {
                            null
                        }
                    }
                    trySend(groups).isSuccess

                }


            }

            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList<Group>()).isSuccess
            }
        }

        groupsRef.addValueEventListener(listener)

        awaitClose {
            groupsRef.removeEventListener(listener)
        }
    }

    override suspend fun updateGroup(groupId: Int?, device: Device.ActionDevice) {
        Log.i("UpdateDevice", "UpdateStarted")
        if (groupId == null) return
        val groupRef = groupsRef.child(userId).child("$groupId")
        val deviceRef = groupRef.child("devices").child(device.id)
        deviceRef.child("status").setValue(!device.status).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("UpdateDevice", "Device status updated successfully.")
            } else {
                Log.e("UpdateDevice", "Failed to update device status.", task.exception)
            }
        }
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
        Log.i("Group", group.toString())
        val groups = getGroupItems()
        Log.i("Group", groups.toString())
        if (!groups.contains(group)) {
            Log.i("Group", group.toString())
            val updatedFavorites = groups.toMutableList().apply { add(group) }
            saveGroups(updatedFavorites)
        }    }

    fun deleteGroupTabel() {
        groupsRef.child(userId).removeValue()

   }
}