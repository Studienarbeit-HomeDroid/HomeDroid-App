package com.homedroid.app.di

import com.google.firebase.database.FirebaseDatabase
import com.homedroid.data.interfaces.IDashboardRepository
import com.homedroid.data.repositories.DashboardRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DashboardRepositoryModule {

    @Provides
    @Singleton
    fun provideDashboardRepository(): IDashboardRepository {
        return DashboardRepository()
    }
}