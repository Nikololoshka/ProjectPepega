package com.vereshchagin.nikolay.stankinschedule

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import com.vereshchagin.nikolay.stankinschedule.core.utils.NotificationUtils
import com.vereshchagin.nikolay.stankinschedule.navigation.*
import com.vereshchagin.nikolay.stankinschedule.ui.AppNavigationBar
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

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                val bottomSheetNavigator = rememberBottomSheetNavigator()
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
                                    JournalNavEntry,
                                    NewsNavEntry
                                )
                            )
                        },
                        contentWindowInsets = ScaffoldDefaults.contentWindowInsets
                            .exclude(WindowInsets.statusBars)
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = HomeNavEntry.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(route = HomeNavEntry.route) {
                                HomeScreen(
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            schedule(navController)
                            moduleJournal(navController)
                            news(navController)
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