package com.homedroidv2.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homedroidv2.data.interfaces.IGroupRepository
import com.homedroidv2.data.model.ParsedDevices
import com.homedroidv2.data.model.ParsedGroup
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
    private val _groups = MutableStateFlow<List<ParsedGroup>>(emptyList())
    val groups: StateFlow<List<ParsedGroup>> = _groups.asStateFlow()

    init {
        viewModelScope.launch {
            groupRepository.getParsedGroupFlow().collect { groupList ->
                Log.d("Firebase Group", "Group exists: $groupList")
                _groups.value = groupList
            }
        }
    }

    fun updateGroup(groupId: Int?, device: ParsedDevices) {
        Log.d("Firebase Group", "Group exists: $device")
        viewModelScope.launch {
            groupRepository.updateDevice(groupId, device)
        }
    }

    fun updateFavorite(groupId: Int?, device: ParsedDevices) {
        viewModelScope.launch {
            groupRepository.updateFavorite(groupId, device)
        }

    }

}