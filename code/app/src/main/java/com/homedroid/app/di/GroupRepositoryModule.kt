package com.homedroid.app.di

import com.google.firebase.database.FirebaseDatabase
import com.homedroid.data.interfaces.IFavoriteRepository
import com.homedroid.data.interfaces.IGroupRepository
import com.homedroid.data.repositories.FavoriteRepository
import com.homedroid.data.repositories.GroupRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object GroupRepositoryModule {

    @Provides
    @Singleton
    fun provideGroupRepository(firebaseDatabase: FirebaseDatabase): IGroupRepository {
        return GroupRepository(firebaseDatabase)
    }
}