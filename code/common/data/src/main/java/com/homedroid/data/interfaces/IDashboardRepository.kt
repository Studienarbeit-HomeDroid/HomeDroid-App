package com.homedroid.data.interfaces

import com.homedroid.data.model.DashboardValues
import kotlinx.coroutines.flow.Flow

interface IDashboardRepository {
    suspend fun getDashboardFlow(): Flow<List<DashboardValues>>
}