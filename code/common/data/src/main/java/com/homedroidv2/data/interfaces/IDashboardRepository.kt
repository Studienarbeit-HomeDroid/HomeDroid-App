package com.homedroidv2.data.interfaces

import com.homedroidv2.data.model.DashboardValues
import kotlinx.coroutines.flow.Flow

interface IDashboardRepository {
    suspend fun getDashboardFlow(): Flow<List<DashboardValues>>
}