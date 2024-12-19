package com.example.places.data

import com.example.places.data.model.Device

val DEVICES = listOf(
    // Physische Geräte
    Device.PhysicalDevice(id = "1", name = "Thermostat", value = "22", unit = "°C"),
    Device.PhysicalDevice(id = "2", name = "Lightbulb", value = "ON", unit = ""),
    Device.PhysicalDevice(id = "3", name = "Fan", value = "Medium", unit = ""),
    Device.PhysicalDevice(id = "4", name = "Refrigerator", value = "5", unit = "°C"),
    Device.PhysicalDevice(id = "5", name = "Oven", value = "180", unit = "°C"),

    // Status-Geräte
    Device.StatusDevice(id = "6", name = "Door Sensor", status = true),
    Device.StatusDevice(id = "7", name = "Window Sensor", status = false),
    Device.StatusDevice(id = "8", name = "Motion Detector", status = true),
    Device.StatusDevice(id = "6", name = "Door Sensor", status = true),
    Device.StatusDevice(id = "7", name = "Window Sensor", status = false),
    Device.StatusDevice(id = "8", name = "Motion Detector", status = true),
    Device.StatusDevice(id = "6", name = "Door Sensor", status = true),
    Device.StatusDevice(id = "7", name = "Window Sensor", status = false),

)

class DeviceRepository {

    fun getDevices(): List<Device>
    {
        return DEVICES;
    }

    fun getDeviceById(deviceId: String): Device?
    {
        return DEVICES.find {it.id == deviceId }
    }
}