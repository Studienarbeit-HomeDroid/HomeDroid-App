package com.homedroid.data.interfaces

import com.homedroid.data.model.Group

interface IGroupRepository {
    suspend fun getGroupItems(): List<Group>
    suspend fun addGroupToList(group: Group)
    suspend fun saveGroups(groups: List<Group>)
}