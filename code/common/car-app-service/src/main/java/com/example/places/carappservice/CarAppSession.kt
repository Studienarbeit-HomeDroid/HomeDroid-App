package com.example.places.carappservice

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session
import com.example.places.carappservice.screen.MainScreen

/**
 * Instance of a client app running on a display in the vehicle
 */
class CarAppSession : Session() {


    override fun onCreateScreen(intent: Intent): Screen {
        // MainScreen will be an unresolved reference until the next step
        return MainScreen(carContext)
    }
}