package com.homedroid.carappservice

import android.util.Log
import androidx.car.app.CarAppService
import androidx.car.app.CarContext
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator
import com.homedroid.carappservice.screen.FavoriteScreen
import com.homedroid.data.repositories.DashboardRepository
import com.homedroid.data.repositories.FavoriteRepository
import com.homedroid.data.repositories.GroupRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CarAppService : CarAppService() {

    @Inject lateinit var favoriteRepository: FavoriteRepository
    @Inject lateinit var dashboardRepository: DashboardRepository
    @Inject lateinit var groupRepository: GroupRepository

    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(): Session {
        Log.d("MyCarAppService", "Creating session with carContext")
        return CarAppSession(favoriteRepository, dashboardRepository, groupRepository)
    }
}

