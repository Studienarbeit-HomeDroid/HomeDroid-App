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
 * Repräsentiert eine Instanz der App im Fahrzeug-Infotainmentsystem (Android Auto).
 *
 * Diese Klasse wird bei Start der App im Fahrzeug erstellt und stellt den Einstiegspunkt
 * für die Anzeige von Inhalten dar.
 */
class CarAppSession(
    private val favoriteRepository: FavoriteRepository,
    private val dashboardRepository: DashboardRepository,
    private val groupRepository: GroupRepository,
) : Session() {


    override fun onCreateScreen(intent: Intent): Screen {
        Log.d("MyCarAppService", "Creating session with carContext: $carContext")
        return MainScreen(carContext, favoriteRepository, dashboardRepository, groupRepository)
    }
}