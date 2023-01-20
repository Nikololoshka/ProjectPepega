package com.vereshchagin.nikolay.stankinschedule.settings.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vereshchagin.nikolay.stankinschedule.core.domain.repository.LoggerAnalytics
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    @Inject
    lateinit var loggerAnalytics: LoggerAnalytics

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "root"
                ) {
                    composable(route = "root") {
                        RootSettingsScreen(
                            viewModel = viewModel,
                            onBackPressed = { onBackPressedDispatcher.onBackPressed() },
                            navigateToSchedule = { navController.navigate("schedule") },
                            navigateToMore = { navController.navigate("more") },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    composable(route = "schedule") {
                        ScheduleSettingsScreen(
                            viewModel = viewModel,
                            onBackPressed = { navController.navigateUp() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    composable(route = "more") {
                        MoreSettingsScreen(
                            viewModel = viewModel,
                            onBackPressed = { navController.navigateUp() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        loggerAnalytics.logEvent(LoggerAnalytics.SCREEN_ENTER, "SettingsActivity")
    }

    override fun onDestroy() {
        super.onDestroy()
        loggerAnalytics.logEvent(LoggerAnalytics.SCREEN_LEAVE, "SettingsActivity")
    }
}