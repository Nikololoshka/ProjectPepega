package com.vereshchagin.nikolay.stankinschedule.navigation.entry

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

abstract class BottomNavEntry(
    route: String,
    @StringRes val nameRes: Int,
    @DrawableRes val iconRes: Int,
    val hierarchy: List<String> = listOf(route)
) : NavigationEntry(route)