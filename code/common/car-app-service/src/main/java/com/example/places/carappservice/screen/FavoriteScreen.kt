package com.example.places.carappservice.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Alert
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarText
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import com.example.places.carappservice.BitMapGenerator
import com.example.places.carappservice.R

class FavoriteScreen(carContext: CarContext) : Screen(carContext) {

    val bitMapGenerator: BitMapGenerator = BitMapGenerator(carContext)

    fun getGridItems(): ItemList {
        val itemList = ItemList.Builder()
        for (i in 1..10)
        {
            val firstItem = GridItem.Builder().setTitle("Bad").setText("Temp").setImage(
                bitMapGenerator.createTextAsIcon("21°C")
            ).setOnClickListener {
                val toast = CarToast.makeText(carContext, "Status wurde aktualisiert", CarToast.LENGTH_SHORT)
                toast.show()
            }.build()

            itemList.addItem(firstItem)
        }

        return itemList.build()
    }

    override fun onGetTemplate(): Template {
        val gridTemplate = GridTemplate.Builder().setTitle("Hello").setLoading(false)
        return gridTemplate.setSingleList(getGridItems()).build()
    }
}