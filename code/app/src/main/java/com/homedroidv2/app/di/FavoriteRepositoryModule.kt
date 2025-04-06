package com.homedroidv2.app.di

import com.google.firebase.database.FirebaseDatabase
import com.homedroidv2.data.interfaces.IFavoriteRepository
import com.homedroidv2.data.repositories.FavoriteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FavoriteRepositoryModule {

    @Provides
    @Singleton
    fun provideFavoriteRepository(firebaseDatabase: FirebaseDatabase): IFavoriteRepository {
        return FavoriteRepository(firebaseDatabase)
    }
}