package com.homedroid.app.viewmodel

import androidx.lifecycle.ViewModel
import com.homedroid.data.repositories.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    val dashboardData = dashboardRepository.dashboardList
}