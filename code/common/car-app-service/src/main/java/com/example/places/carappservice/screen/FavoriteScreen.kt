package com.example.places.carappservice.screen

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import com.example.places.carappservice.BitMapGenerator
import com.example.places.data.repositories.FavoriteRepository
import com.example.places.data.model.Device
import kotlinx.coroutines.runBlocking


class FavoriteScreen(carContext: CarContext) : Screen(carContext) {

    private val bitMapGenerator: BitMapGenerator = BitMapGenerator(carContext)
    private val favoriteRepository: FavoriteRepository = FavoriteRepository()
    private lateinit var itemList: ItemList

    private suspend fun getGridItems(): ItemList {
        try {
            val favorites = favoriteRepository.getFavorites()
            val itemList = ItemList.Builder()
            favorites.map { device ->
                when (device) {
                    is Device.StatusDevice -> {
                        val firstItem =
                            GridItem.Builder().setTitle(device.name).setText(device.description)
                                .setImage(
                                    bitMapGenerator.createTextAsIcon("${device.value}${device.unit}")
                                ).setOnClickListener {
                                showToast()
                            }.build()
                        itemList.addItem(firstItem)
                    }

                    is Device.ActionDevice -> {
                        val firstItem = GridItem.Builder().setTitle(device.name).setImage(
                            bitMapGenerator.createTextAsIcon("${device.status}")
                        ).setOnClickListener {
                            showToast()
                        }.build()
                        itemList.addItem(firstItem)
                    }
                }
            }
            return itemList.build()
        }
        catch (e: Exception)
        {
            Log.e("GET_GRID_ITEMS", "Error loading favorites", e)
            return ItemList.Builder().build()
        }
    }

    override fun onGetTemplate(): Template {
        val items = runBlocking {
            getGridItems()
        }
        itemList = items
        invalidate()
        return GridTemplate.Builder()
            .setLoading(false)
            .setSingleList(itemList)
            .build()
    }

    private  fun showToast() {
        val toast = CarToast.makeText(
            carContext,
            "Status wurde aktualisiert",
            CarToast.LENGTH_SHORT
        )
        toast.show()
    }
}