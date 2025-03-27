package com.homedroidv2.data.interfaces

import com.homedroidv2.data.model.Device
import com.homedroidv2.data.model.Group
import com.homedroidv2.data.model.ParsedDevices
import com.homedroidv2.data.model.ParsedGroup
import kotlinx.coroutines.flow.Flow

interface IGroupRepository {
    suspend fun getGroupItems(): List<Group>
    suspend fun addGroupToList(group: Group)
    suspend fun saveGroups(groups: List<Group>)
    suspend fun getGroupItemsFlow(): Flow<List<Group>>
    suspend fun updateGroup(groupId: Int?, device: Device.ActionDevice)
    suspend fun saveParsedGroups(group: ParsedGroup)
    suspend fun updateDevice(device: ParsedDevices)
    suspend fun getParsedGroupFlow(): Flow<List<ParsedGroup>>
    suspend fun updateFavorite(groupId: Int?, device: ParsedDevices)
}