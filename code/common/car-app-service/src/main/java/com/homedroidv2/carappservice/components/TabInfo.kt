package com.homedroidv2.carappservice.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class TabInfo(
    val tabId: String,
    @StringRes val tabTitle: Int,
    @DrawableRes val tabIcon: Int
)