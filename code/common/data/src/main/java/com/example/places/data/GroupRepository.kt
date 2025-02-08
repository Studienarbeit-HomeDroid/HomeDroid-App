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

package com.example.places.data

import com.example.places.data.model.Group
val deviceRepository: DeviceRepository = DeviceRepository()
val devices = deviceRepository.getDevices();
val GROUPITEMS = listOf(
    Group(
        0,
        "Living Room",
        devices
    ),
    Group(
        1,
        "Kitchen",
        devices
    ),
    Group(
        2,
        "Bedroom",
        devices
    ),
    Group(
        3,
        "Bathroom",
        devices
    ),
    Group(
        4,
        "Dining Room",
        devices
    ),
    Group(
        5,
        "Home Office",
        devices
    ),
    Group(
        6,
        "Hallway",
        devices
    ),
    Group(
        7,
        "Garage",
        devices
    ),
    Group(
        8,
        "Basement",
        devices
    )
)

class GroupRepository {
    fun getGroupItems(): List<Group> {
        return GROUPITEMS
    }

    fun getGroupById(placeId: Int): Group? {
        return GROUPITEMS.find { it.id == placeId }
    }
}