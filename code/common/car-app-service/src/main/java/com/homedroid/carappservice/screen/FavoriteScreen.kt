package com.homedroid.carappservice.screen

import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.homedroid.carappservice.BitMapGenerator
import com.homedroid.data.model.Device
import com.homedroid.data.repositories.FavoriteRepository
import com.homedroid.data.repositories.GroupRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * A screen that displays a list of favorite devices in a grid layout.
 */
@RequiresApi(Build.VERSION_CODES.P)
class FavoriteScreen(
    carContext: CarContext,
    private val favoriteRepository: FavoriteRepository,
    private val groupRepository: GroupRepository,
    private val bitMapGenerator: BitMapGenerator = BitMapGenerator() // Optional: Injizieren
) : Screen(carContext) {



    private var favorites: List<Device> = emptyList() // Aktuelle Favoritenliste
    private var isLoading = true // Ladezustand

    init {
        lifecycleScope.launch {
            favoriteRepository.getFavoritesFlow().collectLatest { newFavorites ->
                if (favorites != newFavorites) {
                    Log.i("FavoriteScreen", "Updating favorites: $newFavorites")
                    favorites = newFavorites
                    isLoading = false
                    withContext(Dispatchers.Main) {
                        invalidate()
                    }
                } else {
                    Log.i("FavoriteScreen", "No change in favorites, skipping update")
                }
            }
        }
    }

    /**
     * Provides the template for the screen.
     *
     * @return A GridTemplate containing the favorite devices or a loading state.
     */
    override fun onGetTemplate(): Template {
        if (isLoading) {
            return GridTemplate.Builder()
                .setTitle("Favoriten")
                .setLoading(true)
                .build()
        }

        val itemList = buildGridItems()
        Log.i("FavoriteScreen", "ItemList: $itemList")

        return GridTemplate.Builder()
            .setTitle("Favoriten")
            .setSingleList(itemList)
            .build()
    }

    /**
     * Builds the grid items representing favorite devices.
     *
     * @return An ItemList containing GridItems for each favorite device.
     */
    private fun buildGridItems(): ItemList {
        val itemListBuilder = ItemList.Builder()
        favorites.forEach { device ->
            when (device) {
                is Device.StatusDevice -> {
                    Log.i("FavoriteScreen", "Adding status device: $device")
                    itemListBuilder.addItem(
                        GridItem.Builder()
                            .setTitle(device.name)
                            .setText(device.description ?: "") // Null-Sicherheit
                            .setImage(bitMapGenerator.createTextAsIcon("${device.value}${device.unit}1"))
                            .setOnClickListener {
                                showToast()
                            }
                            .build()
                    )
                }
                is Device.ActionDevice -> {
                    itemListBuilder.addItem(
                        GridItem.Builder()
                            .setTitle(device.name)
                            .setImage(bitMapGenerator.createTextAsIcon("${device.status}"))
                            .setOnClickListener {
                                CoroutineScope(Dispatchers.IO).launch {
                                    favoriteRepository.updateFavorites(device.groupid.toInt(), device)
                                    groupRepository.updateGroup(device.groupid.toInt(), device)
                                }
                                invalidate()
                            }
                            .build()
                    )
                }
                is Device.TemperatureDevice -> {
                    itemListBuilder.addItem(
                        GridItem.Builder()
                            .setTitle(device.name)
                            .setImage(bitMapGenerator.createTextAsIcon("${device.value}°C")) // Beispiel
                            .setOnClickListener {
                                showToast() }
                            .build()
                    )
                }
            }
        }
        return itemListBuilder.build()
    }

    /**
     * Shows a toast message when an item is clicked.
     */
    private fun showToast() {
        CarToast.makeText(
            carContext,
            "Status wurde aktualisiert",
            CarToast.LENGTH_SHORT
        ).show()
    }
}