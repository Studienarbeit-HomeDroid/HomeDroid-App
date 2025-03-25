package com.homedroid.data.interfaces

import com.homedroid.data.model.Device
import com.homedroid.data.model.Group
import kotlinx.coroutines.flow.Flow

interface IGroupRepository {
    suspend fun getGroupItems(): List<Group>
    suspend fun addGroupToList(group: Group)
    suspend fun saveGroups(groups: List<Group>)
    suspend fun getGroupItemsFlow(): Flow<List<Group>>
    suspend fun updateGroup(groupId: Int?, device: Device.ActionDevice)

}