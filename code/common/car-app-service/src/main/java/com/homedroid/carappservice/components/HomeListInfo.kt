package com.homedroid.carappservice.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class HomeListInfo(
    val listId: String,
    val title: String,
    val desciption: String,
    @DrawableRes val icon: Int
)