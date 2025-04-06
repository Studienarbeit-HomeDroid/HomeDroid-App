package com.homedroidv2.app

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HomeDroidApplication : Application(), ImageLoaderFactory {

    /**
     * Wird aufgerufen, beim Startet der App
     * Initialisiert die Datenbank, sowie die Analyse Tools
     */
    private lateinit var auth: FirebaseAuth
    private lateinit var mTracker: Tracker


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate() {
        super.onCreate()
            FirebaseApp.initializeApp(this)
            FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true

            val analytics = GoogleAnalytics.getInstance(this)
            mTracker = analytics.newTracker("UA-XXXXXXX-Y")
         auth = FirebaseAuth.getInstance()
            signIn()
    }

    /**
     * Führt die Anmeldung eines Benutzers über Firebase Authentication durch.
     *
     * Verwendet eine vordefinierte E-Mail-Adresse und ein Passwort, um sich mit dem
     * Firebase-Authentifizierungsdienst zu verbinden.
     *
     */

    // TODO: Zugangsdaten sind aktuell statisch hinterlegt. Noch in Secret auslagern
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
     * Erstellt und konfiguriert eine neue ImageLoader-Instanz für die Anwendung.
     * Diese Funktion passt das Ladeverhalten für Bilder an, indem sie Speicher- und Festplattencaches einrichtet
     * sowie die Unterstützung für das Dekodieren von SVG-Dateien aktiviert.
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