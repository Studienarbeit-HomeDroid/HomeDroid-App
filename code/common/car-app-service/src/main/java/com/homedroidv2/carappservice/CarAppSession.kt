package com.homedroidv2.carappservice

import android.content.Intent
import android.util.Log
import androidx.car.app.Screen
import androidx.car.app.Session
import com.homedroidv2.carappservice.screen.MainScreen
import com.homedroidv2.data.repositories.DashboardRepository
import com.homedroidv2.data.repositories.FavoriteRepository
import com.homedroidv2.data.repositories.GroupRepository

/**
 * Instance of a client app running on a display in the vehicle
 */
class CarAppSession(
    private val favoriteRepository: FavoriteRepository,
    private val dashboardRepository: DashboardRepository,
    private val groupRepository: GroupRepository,
) : Session() {


    override fun onCreateScreen(intent: Intent): Screen {
        // MainScreen will be an unresolved reference until the next step
        Log.d("MyCarAppService", "Creating session with carContext: $carContext")
        return MainScreen(carContext, favoriteRepository, dashboardRepository, groupRepository)
    }
}