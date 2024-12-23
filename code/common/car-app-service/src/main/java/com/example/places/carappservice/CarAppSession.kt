package com.example.places.carappservice

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.places.ViewModels.FavoriteViewModel
import com.example.places.carappservice.screen.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

//Instance of a client app running on a display in the vehicle
class CarAppSession : Session() {


    override fun onCreateScreen(intent: Intent): Screen {
        // MainScreen will be an unresolved reference until the next step
        return MainScreen(carContext)
    }
}