package com.vereshchagin.nikolay.stankinschedule

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import com.vereshchagin.nikolay.stankinschedule.core.utils.NotificationUtils
import com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.journal.JournalScreen
import com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.login.JournalLoginScreen
import com.vereshchagin.nikolay.stankinschedule.navigation.BottomNavigationEntry
import com.vereshchagin.nikolay.stankinschedule.news.ui.review.NewsReviewScreen
import com.vereshchagin.nikolay.stankinschedule.news.ui.viewer.NewsViewerScreen
import com.vereshchagin.nikolay.stankinschedule.schedule.ui.list.ScheduleCreatorSheet
import com.vereshchagin.nikolay.stankinschedule.schedule.ui.list.ScheduleScreen
import com.vereshchagin.nikolay.stankinschedule.schedule.ui.viewer.ScheduleViewerScreen
import com.vereshchagin.nikolay.stankinschedule.ui.HomeScreen
import dagger.hilt.android.AndroidEntryPoint
import com.vereshchagin.nikolay.stankinschedule.core.R as R_core


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialNavigationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R_core.style.AppTheme)

        super.onCreate(savedInstanceState)
        setupNotifications()

        setContent {
            AppTheme {
                val bottomSheetNavigator = rememberBottomSheetNavigator()
                val navController = rememberNavController(bottomSheetNavigator)

                val screens: List<BottomNavigationEntry> = listOf(
                    BottomNavigationEntry(
                        route = "home",
                        nameRes = R.string.nav_home,
                        iconRes = R.drawable.nav_home
                    ),
                    BottomNavigationEntry(
                        route = "schedule",
                        nameRes = R.string.nav_schedule,
                        iconRes = R.drawable.nav_schedule
                    ),
                    BottomNavigationEntry(
                        route = "journal_content",
                        nameRes = R.string.nav_journal,
                        iconRes = R.drawable.nav_journal
                    ),
                    BottomNavigationEntry(
                        route = "news",
                        nameRes = R.string.nav_news,
                        iconRes = R.drawable.nav_news
                    )
                )
                val navBackStackEntry by navController.currentBackStackEntryAsState()

                ModalBottomSheetLayout(
                    bottomSheetNavigator = bottomSheetNavigator,
                    sheetBackgroundColor = MaterialTheme.colorScheme.background,
                ) {
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                val currentDestination = navBackStackEntry?.destination

                                screens.forEach { screen ->
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                painter = painterResource(screen.iconRes),
                                                contentDescription = null
                                            )
                                        },
                                        label = {
                                            Text(text = stringResource(screen.nameRes))
                                        },
                                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                // Pop up to the start destination of the graph to
                                                // avoid building up a large stack of destinations
                                                // on the back stack as users select items
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                // Avoid multiple copies of the same destination when
                                                // reselecting the same item
                                                launchSingleTop = true
                                                // Restore state when reselecting a previously selected item
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        },
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier
                                .padding(innerPadding)
                        ) {
                            composable("home") {
                                HomeScreen(
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            composable("schedule") {
                                ScheduleScreen(
                                    onScheduleCreate = {
                                        navController.navigate("sheet")
                                    },
                                    onScheduleClicked = { id ->
                                        navController.navigate("schedule/$id")
                                    },
                                    viewModel = hiltViewModel(),
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            composable(
                                route = "schedule/{scheduleId}",
                                arguments = listOf(
                                    navArgument("scheduleId") { type = NavType.LongType }
                                )
                            ) { backStackEntry ->
                                ScheduleViewerScreen(
                                    scheduleId = backStackEntry.arguments?.getLong("scheduleId") ?: -1,
                                    scheduleName = null,
                                    viewModel = hiltViewModel(),
                                    onBackPressed = { navController.navigateUp() },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            bottomSheet("sheet") {
                                ScheduleCreatorSheet(
                                    onNavigateBack = {
                                        navController.popBackStack()
                                    }
                                )
                            }

                            composable("journal_login") {
                                JournalLoginScreen(
                                    viewModel = hiltViewModel(),
                                    navigateToJournal = {
                                        navController.navigate("journal_content") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            composable("journal_content") {
                                JournalScreen(
                                    viewModel = hiltViewModel(),
                                    navigateToLogging = {
                                        navController.navigate("journal_login")
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            composable("news") {
                                NewsReviewScreen(
                                    viewModel = hiltViewModel(),
                                    navigateToViewer = { title, newsId ->
                                        navController.navigate("viewer/$newsId?title=$title")
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            composable(
                                route = "viewer/{newsId}?title={newsTitle}",
                                arguments = listOf(
                                    navArgument("newsId") {
                                        type = NavType.IntType
                                    },
                                    navArgument("newsTitle") {
                                        type = NavType.StringType
                                        nullable = true
                                    }
                                )
                            ) { backStackEntry ->
                                NewsViewerScreen(
                                    title = backStackEntry.arguments?.getString("newsTitle"),
                                    postId = backStackEntry.arguments?.getInt("newsId") ?: -1,
                                    viewModel = hiltViewModel(),
                                    onBackPressed = { navController.navigateUp() },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Устанавливает настройки уведомлений для приложения.
     */
    private fun setupNotifications() {
        // android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // общего назначения
            val channelCommon = NotificationChannel(
                NotificationUtils.CHANNEL_COMMON,
                getString(R_core.string.notification_common),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channelCommon.description = getString(R_core.string.notification_common_description)
            channelCommon.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channelCommon.enableVibration(true)
            channelCommon.enableLights(true)

            // модульного журнала
            val channelModuleJournal = NotificationChannel(
                NotificationUtils.CHANNEL_MODULE_JOURNAL,
                getString(R_core.string.notification_mj),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channelModuleJournal.description = getString(R_core.string.notification_mj_description)
            channelModuleJournal.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channelModuleJournal.enableVibration(true)
            channelModuleJournal.enableLights(true)

            getSystemService(NotificationManager::class.java)?.let { manager ->
                manager.createNotificationChannel(channelCommon)
                manager.createNotificationChannel(channelModuleJournal)
            }
        }
    }

}