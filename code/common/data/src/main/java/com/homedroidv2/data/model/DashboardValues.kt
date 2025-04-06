package com.homedroidv2.data.model

data class DashboardValues(
    val id: String = "",
    val title: String = "",
    val subtitle: List<String> = emptyList(),
    val values: List<String> = emptyList(),
    val unit: String = ""
)