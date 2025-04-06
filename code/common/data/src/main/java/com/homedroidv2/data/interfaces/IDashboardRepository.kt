package com.homedroidv2.data.interfaces

import com.homedroidv2.data.model.DashboardValues
import com.homedroidv2.data.model.HeizungValues
import kotlinx.coroutines.flow.Flow

interface IDashboardRepository {
    suspend fun getDashboardFlow(): Flow<List<DashboardValues>>
    suspend fun updateSolarDatas(newTagesValue: String, newGesamtValue:String)
    suspend fun getHeizungFlow(): Flow<List<HeizungValues>>
    suspend fun updateHeizungValue(id: String, newValue: String)
    suspend fun updateWindowDatas(newUnverriegeltValue: String)
    suspend fun updateOpenDoorDatas(newOffeneDoorValue: String)
    suspend fun updateClosedDoorDatas(newGeschlosseneDoorValue: String)
    suspend fun updateStromDatas(newBezug: String, newZaehler:String)
     suspend fun updateSZaehlerDatas(newBezug: String, newLieferung:String)
    fun saveHeizungValuesList(heizungList: List<HeizungValues>)
    fun saveDashboardValuesList(dashboardList: List<DashboardValues>)
}
