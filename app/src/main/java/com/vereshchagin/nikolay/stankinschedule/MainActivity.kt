package com.vereshchagin.nikolay.stankinschedule

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
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
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.ApplicationTheme
import com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.screen.JournalLoginScreen
import com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.screen.JournalScreen
import com.vereshchagin.nikolay.stankinschedule.navigation.BottomNavigationEntry
import com.vereshchagin.nikolay.stankinschedule.news.ui.screen.NewsReviewScreen
import com.vereshchagin.nikolay.stankinschedule.news.ui.screen.NewsViewerScreen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ApplicationTheme {
                val navController = rememberNavController()
                val screens: List<BottomNavigationEntry> = listOf(
                    BottomNavigationEntry(
                        route = "journal_login",
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

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(text = "AppBar") },
                        )
                    },
                    bottomBar = {
                        BottomNavigation {
                            val currentDestination = navBackStackEntry?.destination

                            screens.forEach { screen ->
                                BottomNavigationItem(
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
                        startDestination = "journal_content",
                        modifier = Modifier
                            .padding(innerPadding)
                    ) {
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
                                navigateToViewer = {
                                    navController.navigate("viewer/$it")
                                },
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
}