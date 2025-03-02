package com.homedroid.data.repositories

import com.homedroid.data.interfaces.IDashboardRepository
import com.homedroid.data.model.DashboardValues
import javax.inject.Inject

class DashboardRepository @Inject constructor() : IDashboardRepository {

    val dashboardList = listOf(
        DashboardValues(id = "1", title = "Haustür", subtitle = listOf("status"), values = listOf("offen")),
        DashboardValues(id = "2", title = "Fenster",subtitle = listOf("unverriegelt"), values = listOf("2")),
        DashboardValues(id = "3", title = "Türen",subtitle = listOf("unverriegelt", "offen"), values = listOf("2", "1")),
        DashboardValues(id = "4", title = "Strom",subtitle = listOf("bezug", "liefeung"), values = listOf("10", "12"), unit = "kwh"),
        DashboardValues(id = "5", title = "Zähler",subtitle = listOf("bezug", "lieferung"), values = listOf("10", "12"), unit = "kwh"),
        DashboardValues(id = "6", title = "Solar",subtitle = listOf("tages", "gesamt"), values = listOf("10", "12"), unit = "kwh")
    )
}