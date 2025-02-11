package com.example.places

import android.app.Application
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp
import java.io.FileReader

@HiltAndroidApp
class HomeDroidApplication : Application(), ImageLoaderFactory {
    /**
     * Called when the application is created.
     * Initializes Firebase Database
     */
    private lateinit var auth: FirebaseAuth

    override fun onCreate() {
        super.onCreate()
            FirebaseApp.initializeApp(this)
            auth = FirebaseAuth.getInstance()
            signIn()
    }

    fun signIn() {
        auth.signInWithEmailAndPassword("user@example.com", "password123")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    println("Anmeldung erfolgreich: ${user?.email}")
                } else {
                    Log.e("Firebase", "Fehler bei der Anmeldung: ${task.exception?.message}", task.exception)
                }
            }
    }

    /**
     * Creates and configures a new ImageLoader instance for the application.
     * This function customizes the image loading behavior by setting up memory and disk caches,
     * SVG decoding support
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.20)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(5 * 1024 * 1024)
                    .build()
            }
            .logger(DebugLogger())
            .components { add(SvgDecoder.Factory())}
            .respectCacheHeaders(true)
            .build()
    }
}