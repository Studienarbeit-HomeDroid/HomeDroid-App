package com.homedroid.data.model

sealed class Device {
    abstract val id: String
    abstract val group: String
    abstract val groupid: String
    abstract val type: String

    data class StatusDevice(
        override val id: String = "",
        val name: String = "",
        val description: String = "",
        val value: String = "1",
        val unit: String = "",
        override val group: String = "",
        override val groupid: String = "",
        override val type: String = ""

    ) : Device() {

    }

    data class ActionDevice(
        override val id: String = "",
        val name: String = "",
        val status: Boolean = false,
        override val group: String = "",
        override val groupid: String = "",
        override val type: String = ""

    ) : Device() {

    }

    data class TemperatureDevice(
        override val id: String = "",
        val name: String = "",
        val value: String = "",
        override val group: String = "",
        override val groupid: String = "",
        override val type: String = ""
        ): Device()
}


