package com.vereshchagin.nikolay.stankinschedule.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

class BottomNavigationEntry(
    route: String,
    @StringRes val nameRes: Int,
    @DrawableRes val iconRes: Int,
) : NavigationEntry(route)