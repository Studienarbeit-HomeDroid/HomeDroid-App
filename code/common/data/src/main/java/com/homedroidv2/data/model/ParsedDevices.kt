package com.homedroidv2.data.model

data class ParsedDevices(
    val deviceType: String = "",
    val raum: String = "",
    val name: String = "",
    val adresse: String = "",
    val parameter1: String = "",
    val htmlId: String = "",
    val messwertTyp: String = "",
    val maxAge: String = "",
    val bfname: String = "",
    val writeAdress: String = "",
    val actionCondition: String = "",
    val action: String = "",
    val queryInterval: String = "",
    val storeChanges: String = "",
    val map2DECT: String = "",
    var value: String = "",
    var status: Boolean = false,
    var favorite: Boolean = false,
    val id: String = "",
    val deviceId: String = ""
)

data class ParsedGroup(
    val id: Int = 0,
    val name: String = "",
    val iconUrl: String? = "",
    var devices: MutableList<ParsedDevices> = mutableListOf()
)

