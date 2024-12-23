package com.example.places.carappservice.screen

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.places.ViewModels.FavoriteViewModel
import com.example.places.carappservice.BitMapGenerator
import com.example.places.data.model.Device
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import javax.inject.Inject
fun Screen.getViewModelStoreOwner(): ViewModelStoreOwner {
    val viewModelStoreOwner = object : ViewModelStoreOwner {
        override val viewModelStore = ViewModelStore()
    }
    lifecycleScope.launch {
        try {
            awaitCancellation()
        } finally {
            viewModelStoreOwner.viewModelStore.clear()
        }
    }
    return viewModelStoreOwner
}


class FavoriteScreen(carContext: CarContext) : Screen(carContext) {

    val bitMapGenerator: BitMapGenerator = BitMapGenerator(carContext)


    val viewModelStoreOwner = getViewModelStoreOwner()
    private val viewModel =  ViewModelProvider(viewModelStoreOwner)[FavoriteViewModel::class]
    val fav = viewModel.favorites.value

    init {
        lifecycleScope.launch {
            viewModel.favorites.collect {
                invalidate()
            }
        }
    }



    fun getGridItems(): ItemList {

        Log.i("ADD TO FAVORITES",
            fav.count().toString()
        )

        val itemList = ItemList.Builder()
        for (device in fav)
        {
            if(device is Device.PhysicalDevice) {
                val firstItem = GridItem.Builder().setTitle(device.name).setText(device.description).setImage(
                    bitMapGenerator.createTextAsIcon("${device.value}${device.unit}")
                ).setOnClickListener {
                    val toast = CarToast.makeText(
                        carContext,
                        "Status wurde aktualisiert",
                        CarToast.LENGTH_SHORT
                    )
                    toast.show()
                }.build()

                itemList.addItem(firstItem)
            }
        }

        return itemList.build()
    }

    override fun onGetTemplate(): Template {
        val gridTemplate = GridTemplate.Builder().setTitle("Hello").setLoading(false)
        return gridTemplate.setSingleList(getGridItems()).build()
    }
}