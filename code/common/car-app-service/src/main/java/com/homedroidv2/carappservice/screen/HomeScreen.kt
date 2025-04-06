package com.homedroidv2.carappservice.screen

import androidx.car.app.CarContext
import androidx.car.app.Screen

import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.lifecycle.lifecycleScope
import android.util.Log
import androidx.annotation.OptIn
import androidx.car.app.annotations.ExperimentalCarApi
import com.homedroidv2.carappservice.R
import com.homedroidv2.carappservice.components.HomeListInfo
import com.homedroidv2.data.model.DashboardValues
import com.homedroidv2.data.repositories.DashboardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * `HomeScreen` zeigt eine Liste von Dashboard-Daten innerhalb der Android Auto Benutzeroberfläche.
 *
 * Diese Klasse verwendet ein ListTemplate, um strukturierte Informationen wie Fensterstatus,
 * Stromverbrauch oder Solardaten als lesbare Textzeilen darzustellen.
 */

class HomeScreen(carContext: CarContext, private val dashboardRepository: DashboardRepository) : Screen(carContext) {
    private val listOfHomeListInfos = mutableListOf<HomeListInfo>()
    private var dashboardData:  List<DashboardValues> = emptyList()
    val itemList = ItemList.Builder()

    init {
       observeFavorites()
    }

    private fun observeFavorites() {
        lifecycleScope.launch {
            dashboardRepository.getDashboardFlow().collectLatest { newDashboard ->
                if (dashboardData != newDashboard) {
                    Log.i("DashboardScreen", "Updating favorites: $newDashboard")
                    dashboardData = newDashboard
                    createListItemsInfos()
                    createItem()
                    withContext(Dispatchers.Main) {
                        invalidate()
                    }
                } else {
                    Log.i("FavoriteScreen", "No change in favorites, skipping update")
                }
            }
        }
    }

    fun createListItemsInfos() {
        listOfHomeListInfos.clear()
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


    @OptIn(ExperimentalCarApi::class)
    fun createItem() {
        itemList.clearItems()
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