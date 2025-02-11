package com.example.places.data.interfaces

import com.example.places.data.model.Group

interface IGroupRepository {
    suspend fun getGroupItems(): List<Group>
    suspend fun addGroupToList(group: Group)
    suspend fun saveGroups(groups: List<Group>)
}