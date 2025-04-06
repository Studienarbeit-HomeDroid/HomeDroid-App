package com.homedroidv2.app.di

import android.content.Context
import com.homedroidv2.data.login.Login
import com.homedroidv2.data.network.HtmlClient
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