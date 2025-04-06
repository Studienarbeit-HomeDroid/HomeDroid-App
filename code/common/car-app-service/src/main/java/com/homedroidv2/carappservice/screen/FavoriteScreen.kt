package com.homedroidv2.carappservice.screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import androidx.lifecycle.lifecycleScope
import com.homedroidv2.carappservice.BitMapGenerator
import com.homedroidv2.data.model.Device
import com.homedroidv2.data.model.ParsedDevices
import com.homedroidv2.data.model.ParsedGroup
import com.homedroidv2.data.repositories.FavoriteRepository
import com.homedroidv2.data.repositories.GroupRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * `FavoriteScreen` zeigt eine Übersicht aller als Favoriten markierten Smart-Home-Geräte im Fahrzeug.
 * Diese Klasse verwendet ein `GridTemplate`, um Geräteinformationen im Rasterformat darzustellen,
 */

@RequiresApi(Build.VERSION_CODES.P)
class FavoriteScreen(
    carContext: CarContext,
    private val favoriteRepository: FavoriteRepository,
    private val groupRepository: GroupRepository,
    private val bitMapGenerator: BitMapGenerator = BitMapGenerator() // Optional: Injizieren
) : Screen(carContext) {



    private var favorites: List<ParsedGroup> = emptyList() // Aktuelle Favoritenliste
    private var isLoading = true // Ladezustand

    init {
        lifecycleScope.launch {
            groupRepository.getParsedGroupFlow().collectLatest { newFavorites ->
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

    private fun buildGridItems(): ItemList {
        val itemListBuilder = ItemList.Builder()
        favorites.forEach { group -> group.devices.filter { it.favorite }.forEach{
            device ->
            when (device.messwertTyp)
            {
                "TLFH","TEMP" -> {
                    itemListBuilder.addItem(
                        GridItem.Builder()
                            .setTitle(device.name)
                            .setText("")
                            .setImage(bitMapGenerator.createTextAsIcon("${device.value}°C"))
                            .setOnClickListener {
                            }
                            .build())
                }

                "S" -> {
                    itemListBuilder.addItem(
                        GridItem.Builder()
                            .setTitle(device.name)
                            .setText("")
                            .setImage(bitMapGenerator.createTextAsIcon(if (device.value == "1") "Off" else "On"))
                            .setOnClickListener {
                                showToast()
                            }
                            .build())
                }
                "F" -> {
                    itemListBuilder.addItem(
                        GridItem.Builder()
                            .setTitle(device.name)
                            .setText("")
                            .setImage(bitMapGenerator.createTextAsIcon(if (device.value == "1") "Zu" else "Offen"))
                            .setOnClickListener {
                                showToast()
                            }
                            .build())
                }
                "T" -> {
                    itemListBuilder.addItem(
                        GridItem.Builder()
                            .setTitle(device.name)
                            .setText("")
                            .setImage(bitMapGenerator.createTextAsIcon(if (device.value == "1") "Zu" else "Offen"))
                            .setOnClickListener {
                                showToast()
                            }
                            .build())
                }
                "V", "PR" -> {
                    itemListBuilder.addItem(
                        GridItem.Builder()
                            .setTitle(device.name)
                            .setText("")
                            .setImage(bitMapGenerator.createTextAsIcon(if (device.value == "1") "Zu" else "Offen"))
                            .setOnClickListener {
                                showToast()
                            }
                            .build())
                }

                else -> {
                    itemListBuilder.addItem(
                        GridItem.Builder()
                            .setTitle(device.name)
                            .setText("")
                            .setImage(bitMapGenerator.createTextAsIcon(device.value))
                            .setOnClickListener {
                            }
                            .build())
                }


            }


        } }
        return itemListBuilder.build()
    }

    /**
     * Zeigt Toast Message wenn angezeigt wird
     */
    private fun showToast() {
        CarToast.makeText(
            carContext,
            "Status wurde aktualisiert",
            CarToast.LENGTH_SHORT
        ).show()
    }
}