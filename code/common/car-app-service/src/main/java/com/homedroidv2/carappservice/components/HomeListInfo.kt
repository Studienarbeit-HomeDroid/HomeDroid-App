package com.homedroidv2.carappservice.components

import androidx.annotation.DrawableRes

data class HomeListInfo(
    val listId: String,
    val title: String,
    val desciption: String,
    @DrawableRes val icon: Int
)