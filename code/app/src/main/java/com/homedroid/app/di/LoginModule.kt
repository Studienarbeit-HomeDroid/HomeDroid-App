package com.homedroid.app.di

import android.content.Context
import com.homedroid.app.di.HtmlClientModule.provideHtmlClient
import com.homedroid.data.login.Login
import com.homedroid.data.network.HtmlClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {
    @Provides
    @Singleton
    fun provideLogin(
        @ApplicationContext context: Context,
        htmlClient: HtmlClient
    ): Login {
        return Login(htmlClient, context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object HtmlClientModule
{
    @Provides
    @Singleton
    fun provideHtmlClient(): HtmlClient {
        return HtmlClient()
    }

}