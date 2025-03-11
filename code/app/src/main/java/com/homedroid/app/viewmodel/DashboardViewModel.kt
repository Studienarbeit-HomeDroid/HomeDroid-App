package com.homedroid.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homedroid.data.model.DashboardData
import com.homedroid.data.model.DashboardValues
import com.homedroid.data.model.Device
import com.homedroid.data.repositories.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _dashboardData = MutableStateFlow<List<DashboardValues>>(emptyList())
    val dashboardData: StateFlow<List<DashboardValues>> = _dashboardData.asStateFlow()

    init {
        viewModelScope.launch {
            dashboardRepository.getDashboardFlow().collect { dashboardList ->
                _dashboardData.value = dashboardList
            }
        }
    }
}