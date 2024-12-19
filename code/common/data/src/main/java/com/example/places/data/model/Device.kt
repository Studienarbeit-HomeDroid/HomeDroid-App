package com.example.places.data.model

sealed class Device {
    abstract val id: String

    data class PhysicalDevice(
        override val id: String,
        val name: String,
        val value: String,
        val unit: String
    ) : Device()

    data class StatusDevice(
        override val id: String,
        val name: String,
        val status: Boolean
    ) : Device()
}


