package com.homedroidv2.app.di

import com.google.firebase.database.FirebaseDatabase
import com.homedroidv2.data.interfaces.IDashboardRepository
import com.homedroidv2.data.repositories.DashboardRepository
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
    fun provideDashboardRepository(firebaseDatabase: FirebaseDatabase): IDashboardRepository {
        return DashboardRepository(firebaseDatabase)
    }
}