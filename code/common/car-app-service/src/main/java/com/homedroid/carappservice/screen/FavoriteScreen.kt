package com.homedroid.carappservice.screen

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import com.homedroid.carappservice.BitMapGenerator
import com.homedroid.data.repositories.FavoriteRepository
import com.homedroid.data.model.Device
import kotlinx.coroutines.runBlocking

/**
 * A screen that displays a list of favorite devices in a grid layout.
 */

class FavoriteScreen(carContext: CarContext) : Screen(carContext) {

    private val bitMapGenerator: BitMapGenerator = BitMapGenerator()
    private val favoriteRepository: FavoriteRepository = FavoriteRepository()
    private lateinit var itemList: ItemList.Builder

    /**
     * Retrieves the grid items representing favorite devices.
     *
     * @return An ItemList containing GridItems for each favorite device.
     */
    private suspend fun getGridItems(): ItemList {
        try {
            val favorites = favoriteRepository.getFavorites()
            Log.d("GET_GRID_ITEMS", "Favorites: $favorites")
            itemList = ItemList.Builder()
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

                    is Device.TemperatureDevice -> {}
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

    /**
     * Provides the template for the screen.
     *
     * @return A GridTemplate containing the favorite devices.
     */

    override fun onGetTemplate(): Template {
        val items: ItemList
        runBlocking {
            items = getGridItems()
        }
        Log.i("GET_GRID_ITEMS", "itemList: ${items}")
        invalidate()
        return GridTemplate.Builder()
            .setLoading(false)
            .setSingleList(items)
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