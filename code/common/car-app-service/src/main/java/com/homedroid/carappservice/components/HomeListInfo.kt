package com.homedroid.carappservice.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class HomeListInfo(
    val listId: String,
    @StringRes val title: Int,
    val desciption: String,
    @DrawableRes val icon: Int
)