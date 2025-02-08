package com.example.places.carappservice.screen

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.CarIcon
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import com.example.places.carappservice.R
import com.example.places.carappservice.components.HomeListInfo

class HomeScreen(carContext: CarContext) : Screen(carContext) {
    private val listOfHomeListInfos = mutableListOf<HomeListInfo>()
    val  itemList = ItemList.Builder()

    init {
        createListItemsInfos()
        createItem()
    }


    //Liste sollte nicht Hard gecoded erzeugt werden
    fun createListItemsInfos()
    {
        listOfHomeListInfos.addAll(
            listOf(
                HomeListInfo("maindoor", R.string.maindoor, "offen", R.drawable.home_tab),
                HomeListInfo("windows", R.string.windows, "unverriegelt", R.drawable.home_tab),
                HomeListInfo("door", R.string.door, "unverriegelt: 0 | offen: 1", R.drawable.home_tab),
                HomeListInfo("elekticity", R.string.elekticity, "bezug: 123 | lieferung: 12", R.drawable.home_tab),
                HomeListInfo("counter", R.string.counter, "bezug: 123 | lieferung: 12", R.drawable.home_tab),
                HomeListInfo("solar", R.string.solar, "tages: 12 | gesamt: 17", R.drawable.home_tab)
            )
        )
    }


    fun createItem()
    {
        println(listOfHomeListInfos.size)
        for (item in listOfHomeListInfos)
        {
            val item = Row.Builder()
                .setTitle(carContext.getString(item.title))
                .setImage(
                    CarIcon.Builder(IconCompat.createWithResource(carContext, item.icon)).build()
                ).addText(item.desciption).build()

            itemList.addItem(item)

        }

    }


    override fun onGetTemplate(): Template {
        val listTemplate = ListTemplate.Builder().setSingleList(itemList.build()).build()
        return listTemplate
    }
}