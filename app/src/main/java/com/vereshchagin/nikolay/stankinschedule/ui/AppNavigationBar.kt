package com.vereshchagin.nikolay.stankinschedule.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.vereshchagin.nikolay.stankinschedule.navigation.entry.BottomNavEntry

@Composable
fun AppNavigationBar(
    navBackStackEntry: NavBackStackEntry?,
    navController: NavController,
    screens: List<BottomNavEntry>,
) = NavigationBar {
    val currentDestination = navBackStackEntry?.destination

    screens.forEach { screen ->

        val isSelected by derivedStateOf {
            currentDestination?.route?.startsWith(screen.route) == true
        }

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(screen.iconRes),
                    contentDescription = null
                )
            },
            label = { Text(text = stringResource(screen.nameRes)) },
            selected = isSelected,
            onClick = {
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = !isSelected || currentDestination?.route == screen.route
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}
