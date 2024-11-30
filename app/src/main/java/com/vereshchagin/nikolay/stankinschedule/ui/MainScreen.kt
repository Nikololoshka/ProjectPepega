package com.vereshchagin.nikolay.stankinschedule.ui

import android.content.Intent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.vereshchagin.nikolay.stankinschedule.home.ui.HomeScreen
import com.vereshchagin.nikolay.stankinschedule.navigation.HomeNavEntry
import com.vereshchagin.nikolay.stankinschedule.navigation.JournalNavEntry
import com.vereshchagin.nikolay.stankinschedule.navigation.NewsNavEntry
import com.vereshchagin.nikolay.stankinschedule.navigation.ScheduleNavEntry
import com.vereshchagin.nikolay.stankinschedule.navigation.ScheduleViewerNavEntry
import com.vereshchagin.nikolay.stankinschedule.navigation.moduleJournal
import com.vereshchagin.nikolay.stankinschedule.navigation.schedule
import com.vereshchagin.nikolay.stankinschedule.news.viewer.ui.NewsViewerActivity
import com.vereshchagin.nikolay.stankinschedule.settings.ui.SettingsActivity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val bottomSheetNavigator = rememberBottomSheetNavigator()

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val showSnackBarState: (message: String) -> Unit = { message ->
        scope.launch { snackBarHostState.showSnackbar(message) }
    }

    val navController = rememberNavController(bottomSheetNavigator)
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        sheetBackgroundColor = MaterialTheme.colorScheme.background,
    ) {
        Scaffold(
            bottomBar = {
                AppNavigationBar(
                    navBackStackEntry = navBackStackEntry,
                    navController = navController,
                    screens = listOf(
                        HomeNavEntry,
                        ScheduleNavEntry,
                        JournalNavEntry
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackBarHostState) },
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets
                .exclude(WindowInsets.statusBars)
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = HomeNavEntry.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = HomeNavEntry.route) {

                    val context = LocalContext.current

                    HomeScreen(
                        viewModel = hiltViewModel(),
                        navigateToSchedule = { scheduleId ->
                            navController.navigate(
                                route = ScheduleViewerNavEntry.routeWithArgs(
                                    scheduleId
                                )
                            ) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                            }
                        },
                        navigateToNews = {
                            navController.navigate(route = NewsNavEntry.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                            }
                        },
                        navigateToNewsPost = { newsTitle, newsId ->
                            context.startActivity(
                                NewsViewerActivity.createIntent(
                                    context,
                                    newsTitle,
                                    newsId
                                )
                            )
                        },
                        navigateToSettings = {
                            context.startActivity(
                                Intent(context, SettingsActivity::class.java)
                            )
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                schedule(navController, showSnackBarState)
                moduleJournal(navController)
                // news(navController)
            }
        }
    }
}