package com.vereshchagin.nikolay.stankinschedule.navigation.entry

import androidx.navigation.NamedNavArgument

abstract class DestinationNavEntry(
    route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) : NavigationEntry(route)