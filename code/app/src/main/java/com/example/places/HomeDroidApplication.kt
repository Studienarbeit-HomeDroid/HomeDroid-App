package com.example.places

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HomeDroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
            FirebaseApp.initializeApp(this)

    }
}