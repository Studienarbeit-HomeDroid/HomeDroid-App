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

/**
 * ViewModel zur Verwaltung von Gerätegruppen innerhalb der Anwendung.
 *
 * Diese Klasse stellt über ein StateFlow eine beobachtbare Liste von `ParsedGroup`-Objekten bereit,
 * Änderungen werden automatisch über einen Flow empfangen und im UI dargestellt.
 *
 * */
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

    /**
     * Aktualisiert ein Gerät innerhalb einer Gruppe.
     */

    fun updateGroup(groupId: Int?, device: ParsedDevices) {
        Log.d("Firebase Group", "Group exists: $device")
        viewModelScope.launch {
            groupRepository.updateDevice(groupId, device)
        }
    }

    /**
     * Aktualisiert die Favoritenstatus einer Gruppe.
     */
    fun updateFavorite(groupId: Int?, device: ParsedDevices) {
        viewModelScope.launch {
            groupRepository.updateFavorite(groupId, device)
        }

    }

}