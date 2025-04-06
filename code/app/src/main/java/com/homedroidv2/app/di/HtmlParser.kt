package com.homedroidv2.app.di

import android.content.Context
import com.homedroidv2.data.network.HtmlClient
import com.homedroidv2.data.parser.HtmlParser
import com.homedroidv2.data.repositories.DashboardRepository
import com.homedroidv2.data.repositories.GroupRepository
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
        dashboardRepository: DashboardRepository,
        htmlClient: HtmlClient,
        groupRepository: GroupRepository
    ): HtmlParser {
        return HtmlParser(
            context, dashboardRepository, groupRepository, htmlClient
        )
    }
}