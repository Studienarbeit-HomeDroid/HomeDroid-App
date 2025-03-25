package com.homedroid.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homedroid.data.interfaces.IFavoriteRepository
import com.homedroid.data.interfaces.IGroupRepository
import com.homedroid.data.model.Device
import com.homedroid.data.model.Group
import com.homedroid.data.repositories.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: IGroupRepository

): ViewModel() {
    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    init {
        viewModelScope.launch {
            groupRepository.getGroupItemsFlow().collect { groupList ->
                Log.d("Firebase Group", "Group exists: $groupList")
                _groups.value = groupList
            }
        }
    }

    fun updateGroup(groupId: Int?, device: Device.ActionDevice) {
        Log.d("Firebase Group", "Group exists: $device")
        viewModelScope.launch {
            groupRepository.updateGroup(groupId, device)
        }
    }

}