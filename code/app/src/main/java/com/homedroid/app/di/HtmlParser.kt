package com.homedroid.app.di

import android.content.Context
import com.homedroid.data.network.HtmlClient
import com.homedroid.data.parser.HtmlParser
import com.homedroid.data.repositories.GroupRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HtmlParserModule {

    @Provides
    @Singleton
    fun provideHtmlParser(
        @ApplicationContext context: Context,
        htmlClient: HtmlClient,
        groupRepository: GroupRepository
    ): HtmlParser {
        return HtmlParser(
            context, groupRepository, htmlClient
        )
    }
}