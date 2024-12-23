package com.example.places.data.model

sealed class Device {
    abstract val id: String
    abstract val isFavorite: Boolean

    data class PhysicalDevice(
        override val id: String = "",
        val name: String = "",
        val description: String = "",
        val value: String = "",
        val unit: String = "",
        override val isFavorite: Boolean = false
    ) : Device()

    // No-Argument Konstruktor hinzufügen
    data class ActionDevice(
        override val id: String = "",
        val name: String = "",
        val status: Boolean = false,
        override val isFavorite: Boolean = false
    ) : Device()
}


