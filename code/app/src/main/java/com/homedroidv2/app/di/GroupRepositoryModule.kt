package com.homedroidv2.app.di

import com.google.firebase.database.FirebaseDatabase
import com.homedroidv2.data.interfaces.IGroupRepository
import com.homedroidv2.data.repositories.GroupRepository
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