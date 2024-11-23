package com.example.places.carappservice.module

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class TabInfo(
    val tabId: String,
    @StringRes val tabTitle: Int,
    @DrawableRes val tabIcon: Int
)