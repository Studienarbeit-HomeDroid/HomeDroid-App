package com.homedroid.carappservice

import android.content.Intent
import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.Session
import com.homedroid.carappservice.screen.MainScreen
import com.homedroid.data.repositories.DashboardRepository
import com.homedroid.data.repositories.FavoriteRepository

/**
 * Instance of a client app running on a display in the vehicle
 */
class CarAppSession(
    private val favoriteRepository: FavoriteRepository,
    private val dashboardRepository: DashboardRepository
) : Session() {


    override fun onCreateScreen(intent: Intent): Screen {
        // MainScreen will be an unresolved reference until the next step
        Log.d("MyCarAppService", "Creating session with carContext: $carContext")
        return MainScreen(carContext, favoriteRepository, dashboardRepository)
    }
}