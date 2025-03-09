package com.homedroid.carappservice.screen

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarText
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import com.homedroid.carappservice.R
import com.homedroid.carappservice.components.HomeListInfo
import com.homedroid.data.repositories.DashboardRepository

class HomeScreen(carContext: CarContext, private val dashboardRepository: DashboardRepository) : Screen(carContext) {
    private val listOfHomeListInfos = mutableListOf<HomeListInfo>()
    private val dashboardData = dashboardRepository.dashboardList
    val itemList = ItemList.Builder()

    init {
        createListItemsInfos()
        createItem()
    }

    fun createListItemsInfos() {

        dashboardData.forEach{ data ->
            var description: String = ""
            if (data.values.isNotEmpty()) {
                description = if (data.subtitle.size == 2) {
                    val unitText = if (data.unit.isNotEmpty()) " ${data.unit}" else ""
                    "${data.subtitle[0]}: ${data.values[0]}$unitText | ${data.subtitle[1]}: ${data.values[1]}"
                } else {
                    // Für den Fall, dass nur ein Untertitel existiert
                    val unitText = if (data.unit.isNotEmpty()) " ${data.unit}" else ""
                    "${data.subtitle[0]}: ${data.values[0]}$unitText"
                }
            }

            val item = HomeListInfo(data.id, data.title, description, R.drawable.home_tab)
            listOfHomeListInfos.add(item)
        }
    }


    fun createItem() {
        println(listOfHomeListInfos.size)
        for (item in listOfHomeListInfos) {
            val item = Row.Builder()
                .setTitle(item.title)
                .addText(item.desciption).build()
            itemList.addItem(item)
        }
    }

    override fun onGetTemplate(): Template {
        val listTemplate = ListTemplate.Builder().setSingleList(itemList.build()).build()
        return listTemplate
    }
}