package com.vereshchagin.nikolay.stankinschedule

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vereshchagin.nikolay.stankinschedule.news.ui.screen.NewsReviewScreen
import com.vereshchagin.nikolay.stankinschedule.news.ui.screen.NewsViewerScreen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TestingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "news"
                ) {
                    composable("news") {
                        NewsReviewScreen(
                            navController = navController,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    composable(
                        route = "viewer/{newsId}",
                        arguments = listOf(navArgument("newsId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        NewsViewerScreen(
                            newsId = backStackEntry.arguments?.getInt("newsId") ?: -1,
                            viewModel = hiltViewModel(),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}