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

package com.homedroidv2.data.repositories

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.homedroidv2.data.interfaces.IGroupRepository
import com.homedroidv2.data.model.Device
import com.homedroidv2.data.model.Group
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.homedroidv2.data.model.ParsedDevices
import com.homedroidv2.data.model.ParsedGroup
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


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




    override suspend fun getParsedGroupFlow(): Flow<List<ParsedGroup>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val groups = snapshot.children.mapNotNull { groupSnapshot ->
                    val name = groupSnapshot.child("name").getValue(String::class.java)
                    val iconUrl = groupSnapshot.child("iconUrl").getValue(String::class.java)
                    val id = groupSnapshot.child("id").getValue(Int::class.java)

                    val devices = groupSnapshot.child("devices").children.mapNotNull { deviceSnapshot ->
                        deviceSnapshot.getValue(ParsedDevices::class.java)
                    }

                    if (name != null && id != null) {
                        ParsedGroup(
                            id = id,
                            name = name,
                            iconUrl = iconUrl,
                            devices = devices.toMutableList()
                        )
                    } else null
                }

                trySend(groups).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList<ParsedGroup>()).isSuccess
            }
        }

        FirebaseDatabase.getInstance()
            .getReference("parserGroups")
            .addValueEventListener(listener)

        awaitClose {
            FirebaseDatabase.getInstance()
                .getReference("parserGroups")
                .removeEventListener(listener)
        }
    }


    override suspend fun saveParsedGroups(group: ParsedGroup) {
        FirebaseDatabase.getInstance()
            .getReference("parserGroups")
            .child(group.id.toString())
            .setValue(group)
            .await()
    }



    override suspend fun updateDevice(groupId: Int?, device: ParsedDevices) {
        Log.i("UpdateDevice", "UpdateStarted")
        Log.i("UpdateDevice", "UpdateStarted")
        Log.i("UpdateDevice", "GroupId: $groupId")
        Log.i("UpdateDevice", "Device: $device")

        Log.i("UpdateDevice", "UpdateStarted")

        if (groupId == null || device.deviceId.isEmpty()) return

        val groupRef = FirebaseDatabase.getInstance().getReference("parserGroups").child(groupId.toString())
        val deviceRef = groupRef.child("devices").child((device.deviceId.toInt()-1).toString())

        deviceRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val currentFavoriteStatus = snapshot.child("status").getValue(Boolean::class.java) ?: false
                deviceRef.child("status").setValue(!currentFavoriteStatus).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i("UpdateDevice", "Device favorite status updated successfully.")
                    } else {
                        Log.e("UpdateDevice", "Failed to update device favorite status.", task.exception)
                    }
                }
            } else {
                Log.e("UpdateDevice", "Device not found.")
            }
        }.addOnFailureListener { exception ->
            Log.e("UpdateDevice", "Failed to fetch device data: ${exception.message}")
        }
    }

    suspend fun getStromDevices(): List<ParsedDevices> = suspendCoroutine { cont ->
        val groupRef = FirebaseDatabase.getInstance().getReference("parserGroups")

        groupRef.get().addOnSuccessListener { snapshot ->
            val stromDevices = mutableListOf<ParsedDevices>()

            for (group in snapshot.children) {
                val groupName = group.child("name").getValue(String::class.java)
                if (groupName.equals("Strom", ignoreCase = true)) {
                    val devicesSnapshot = group.child("devices")
                    for (deviceChild in devicesSnapshot.children) {
                        val device = deviceChild.getValue(ParsedDevices::class.java)
                        if (device != null) {
                            stromDevices.add(device)
                        }
                    }
                    break // Wenn nur eine "Strom"-Gruppe existiert
                }
            }

            cont.resume(stromDevices)
        }.addOnFailureListener { exception ->
            Log.e("GetStromDevices", "Fehler beim Laden: ${exception.message}")
            cont.resume(emptyList())
        }
    }

    override suspend fun updateFavorite(groupId: Int?, device: ParsedDevices) {
        Log.i("UpdateFavorite", "Update started")
        Log.i("UpdateFavorite", "GroupId: $groupId")
        Log.i("UpdateFavorite", "Device: $device")

        if (device.deviceId.isEmpty()) return

        val groupRef = FirebaseDatabase.getInstance()
            .getReference("parserGroups")
            .child(groupId.toString())

        val devicesRef = groupRef.child("devices")

        devicesRef.get().addOnSuccessListener { snapshot ->
            var found = false

            for (child in snapshot.children) {
                val firebaseDevice = child.getValue(ParsedDevices::class.java)
                if (firebaseDevice?.deviceId == device.deviceId) {
                    val deviceRef = child.ref
                    Log.i("UpdateFavorite", "DeviceRef: $deviceRef")

                    val currentFavoriteStatus = child.child("favorite").getValue(Boolean::class.java) ?: false
                    deviceRef.child("favorite").setValue(!currentFavoriteStatus).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.i("UpdateFavorite", "Device favorite status updated to ${!currentFavoriteStatus}")
                        } else {
                            Log.e("UpdateFavorite", "Failed to update favorite status.", task.exception)
                        }
                    }

                    found = true
                    break
                }
            }

            if (!found) {
                Log.e("UpdateFavorite", "Device with matching deviceId not found.")
            }
        }.addOnFailureListener { exception ->
            Log.e("UpdateFavorite", "Failed to fetch devices: ${exception.message}")
        }
    }


    override suspend fun updateDeviceValue(groupId: Int, device: ParsedDevices, newValue: String) {
        Log.i("UpdateDevice", "UpdateStarted")
        Log.i("UpdateDevice", "GroupId: $groupId")
        Log.i("UpdateDevice", "Device: $device")
        Log.i("UpdateDevice", "Value: $newValue")

        if (device.deviceId.isEmpty()) return

        val groupRef = FirebaseDatabase.getInstance()
            .getReference("parserGroups")
            .child(groupId.toString())

        val devicesRef = groupRef.child("devices")

        devicesRef.get().addOnSuccessListener { snapshot ->
            var found = false

            for (child in snapshot.children) {
                val firebaseDevice = child.getValue(ParsedDevices::class.java)
                if (firebaseDevice?.deviceId == device.deviceId) {
                    val deviceRef = child.ref
                    Log.i("UpdateDeviceRef", "DeviceRef: $deviceRef")

                    deviceRef.child("value").setValue(newValue).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.i("UpdateDevice", "Device value updated successfully.")
                        } else {
                            Log.e("UpdateDevice", "Failed to update device value.", task.exception)
                        }
                    }

                    found = true
                    break
                }
            }

            if (!found) {
                Log.e("UpdateDevice", "Device with matching deviceId not found.")
            }
        }.addOnFailureListener { exception ->
            Log.e("UpdateDevice", "Failed to fetch devices: ${exception.message}")
        }
    }

    override suspend fun getNumberOfOpenWindows(): Int {
        val snapshot = FirebaseDatabase.getInstance()
            .getReference("parserGroups")
            .get()
            .await()

        var count = 0

        snapshot.children.forEach { groupSnapshot ->
            val devicesSnapshot = groupSnapshot.child("devices")
            devicesSnapshot.children.forEach { deviceSnapshot ->
                val messwertTyp = deviceSnapshot.child("messwertTyp").getValue(String::class.java)
                val value = deviceSnapshot.child("value").getValue(String::class.java)

                if (messwertTyp == "F" && value == "0") {
                    Log.i("OPEN WINDOWS", "device found: ${deviceSnapshot}")
                    count++
                }
            }
        }

        return count
    }

    override suspend fun getNumberOfOpenDoors(): Int {
        val snapshot = FirebaseDatabase.getInstance()
            .getReference("parserGroups")
            .get()
            .await()

        var count = 0

        snapshot.children.forEach { groupSnapshot ->
            val devicesSnapshot = groupSnapshot.child("devices")
            devicesSnapshot.children.forEach { deviceSnapshot ->
                val messwertTyp = deviceSnapshot.child("messwertTyp").getValue(String::class.java)
                val value = deviceSnapshot.child("value").getValue(String::class.java)

                if (messwertTyp == "T" && value == "0") {
                    count++
                }
            }
        }

        return count
    }

    override suspend fun getNumberOfUnlockedDoors(): Int {
        val snapshot = FirebaseDatabase.getInstance()
            .getReference("parserGroups")
            .get()
            .await()

        var count = 0

        snapshot.children.forEach { groupSnapshot ->
            val devicesSnapshot = groupSnapshot.child("devices")
            devicesSnapshot.children.forEach { deviceSnapshot ->
                val messwertTyp = deviceSnapshot.child("messwertTyp").getValue(String::class.java)
                val value = deviceSnapshot.child("value").getValue(String::class.java)

                if ((messwertTyp == "V" || messwertTyp == "PR") && value == "0") {
                    Log.i("UNLOCKED DOOR", "Group found: $value")
                    count++
                }
            }
        }

        return count
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