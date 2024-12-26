package com.example.places.data

import com.example.places.data.model.Device

val DEVICES = listOf(
    Device.StatusDevice(
        id = "1",
        name = "Thermostat",
        description = "Temperature",
        value = "22",
        unit = "°C",
        isFavorite = false
    ),
    Device.StatusDevice(
        id = "2",
        name = "Lightbulb",
        description = "Status",
        value = "ON",
        unit = "",
        isFavorite = false
    ),
    Device.StatusDevice(
        id = "3",
        name = "Fan",
        description = "Speed",
        value = "Medium",
        unit = "",
        isFavorite = false
    ),
    Device.StatusDevice(
        id = "4",
        name = "Refrigerator",
        description = "Temperature",
        value = "5",
        unit = "°C",
        isFavorite = false
    ),
    Device.StatusDevice(
        id = "5",
        name = "Oven",
        description = "Temperature",
        value = "180",
        unit = "°C",
        isFavorite = false
    ),

    Device.ActionDevice(id = "6", name = "Door Sensor", status = true, isFavorite = false),
    Device.ActionDevice(id = "7", name = "Window Sensor", status = false, isFavorite = false),
    Device.ActionDevice(id = "8", name = "Motion Detector", status = true, isFavorite = false),
    Device.ActionDevice(id = "6", name = "Door Sensor", status = true, isFavorite = false),
    Device.ActionDevice(id = "7", name = "Window Sensor", status = false, isFavorite = false),
    Device.ActionDevice(id = "8", name = "Motion Detector", status = true, isFavorite = false),
    Device.ActionDevice(id = "6", name = "Door Sensor", status = true, isFavorite = false),
    Device.ActionDevice(id = "7", name = "Window Sensor", status = false, isFavorite = false)

)

class DeviceRepository {

    fun getDevices(): List<Device> {
        return DEVICES;
    }

    fun getDeviceById(deviceId: String): Device? {
        return DEVICES.find { it.id == deviceId }
    }
}