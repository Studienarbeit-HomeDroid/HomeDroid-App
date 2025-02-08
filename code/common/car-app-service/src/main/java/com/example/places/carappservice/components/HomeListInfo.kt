package com.example.places.carappservice.components

import android.icu.text.CaseMap.Title
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.car.app.model.CarIcon

data class HomeListInfo(
    val listId: String,
    @StringRes val title: Int,
    val desciption: String,
    @DrawableRes val icon: Int
)