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

import com.example.places.data.model.Device
import com.example.places.data.model.Group

val deviceRepository: DeviceRepository = DeviceRepository()
val devices = deviceRepository.getDevices();
val mutableGroupsItems: MutableList<Group> = mutableListOf()


class GroupRepository {
    fun getGroupItems(): List<Group> {
        return mutableGroupsItems
    }

    fun getGroupDevices(): List<Device> {
        return devices
    }

    fun addGroupToList(group: Group){
        mutableGroupsItems.add(group)
    }

//    fun getGroupById(placeId: Int): Group? {
//        return GROUPITEMS.find { it.id == placeId }
//    }
}