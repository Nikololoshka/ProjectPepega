package com.vereshchagin.nikolay.stankinschedule.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.journal.JournalScreen
import com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.login.JournalLoginScreen
import com.vereshchagin.nikolay.stankinschedule.navigation.entry.BottomNavEntry
import com.vereshchagin.nikolay.stankinschedule.navigation.entry.DestinationNavEntry


object JournalLoginNavEntry : DestinationNavEntry(
    route = "journal/login"
)

object JournalNavEntry : BottomNavEntry(
    route = "journal",
    nameRes = R.string.nav_journal,
    iconRes = R.drawable.nav_journal,
    hierarchy = listOf("journal", JournalLoginNavEntry.route)
)

fun NavGraphBuilder.moduleJournal(navController: NavController) {
    // Авторизация
    composable(route = JournalLoginNavEntry.route) {
        JournalLoginScreen(
            viewModel = hiltViewModel(),
            navigateToJournal = {
                navController.navigate(JournalNavEntry.route) {
                    // Убрать экран с логином
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
    // Просмотр журнала
    composable(JournalNavEntry.route) {
        JournalScreen(
            viewModel = hiltViewModel(),
            navigateToLogging = {
                navController.navigate(JournalLoginNavEntry.route)
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}