package com.homedroidv2.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homedroidv2.data.model.DashboardValues
import com.homedroidv2.data.model.HeizungValues
import com.homedroidv2.data.repositories.DashboardRepository
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

    private val _heizungData = MutableStateFlow<List<HeizungValues>>(emptyList())
    val heizungData: StateFlow<List<HeizungValues>> = _heizungData.asStateFlow()

    init {
        viewModelScope.launch {
            dashboardRepository.getHeizungFlow().collect { heizungList ->
                _heizungData.value = heizungList
            }
        }

        viewModelScope.launch {
            dashboardRepository.getDashboardFlow().collect { dashboardList ->
                Log.d("DashboardViewModel", "Received dashboard data: $dashboardList")
                _dashboardData.value = dashboardList
            }
        }


    }
}