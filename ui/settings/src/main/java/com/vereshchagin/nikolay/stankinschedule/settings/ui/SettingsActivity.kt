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
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

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
                            navigateToSchedule = {
                                navController.navigate("schedule")
                            },
                            navigateToWidgets = {

                            },
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
                }
            }
        }
    }
}