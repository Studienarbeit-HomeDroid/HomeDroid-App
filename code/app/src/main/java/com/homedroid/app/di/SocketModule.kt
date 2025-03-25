package com.homedroid.app.di

import com.google.firebase.database.FirebaseDatabase
import com.homedroid.data.network.Socket
import com.homedroid.data.repositories.DashboardRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object SocketModule {
//
//    @Provides
//    @Singleton
//    fun provideSocket(dashboardRepository: DashboardRepository): Socket {
//        return Socket(dashboardRepository )
//
//    }
//}