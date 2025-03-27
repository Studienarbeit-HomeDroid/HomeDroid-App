package com.homedroid.data.model

data class DashboardData(
    val id: String,
    val title: String,
    val subtitle: List<String>,
    val values: List<String>,
    val unit: String = ""
)
