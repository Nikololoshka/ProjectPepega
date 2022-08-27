package com.vereshchagin.nikolay.stankinschedule.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.navigation.entry.BottomNavEntry
import com.vereshchagin.nikolay.stankinschedule.navigation.entry.DestinationNavEntry
import com.vereshchagin.nikolay.stankinschedule.news.ui.review.NewsReviewScreen
import com.vereshchagin.nikolay.stankinschedule.news.ui.viewer.NewsViewerScreen

object NewsNavEntry : BottomNavEntry(
    route = "news",
    nameRes = R.string.nav_news,
    iconRes = R.drawable.nav_news,
    hierarchy = listOf("news", NewsViewerNavEntry.route)
)

object NewsViewerNavEntry : DestinationNavEntry(
    route = "news/{newsId}?title={newsTitle}",
    arguments = listOf(
        navArgument("newsId") {
            type = NavType.IntType
        },
        navArgument("newsTitle") {
            type = NavType.StringType
            nullable = true
        }
    )
) {
    fun routeWithArgs(newsId: Int, newsTitle: String?) = "news/$newsId?title=$newsTitle"

    fun parseNewsId(entry: NavBackStackEntry) = entry.arguments?.getInt("newsId") ?: -1
    fun parseNewsTitle(entry: NavBackStackEntry) = entry.arguments?.getString("newsTitle")
}

fun NavGraphBuilder.news(navController: NavController) {
    // Новости
    composable(route = NewsNavEntry.route) {
        NewsReviewScreen(
            viewModel = hiltViewModel(),
            navigateToViewer = { newsTitle, newsId ->
                navController.navigate(
                    route = NewsViewerNavEntry.routeWithArgs(newsId, newsTitle)
                )
            },
            modifier = Modifier.fillMaxSize()
        )
    }
    // Просмотр новости
    composable(
        route = NewsViewerNavEntry.route,
        arguments = NewsViewerNavEntry.arguments
    ) { backStackEntry ->
        NewsViewerScreen(
            title = NewsViewerNavEntry.parseNewsTitle(backStackEntry),
            postId = NewsViewerNavEntry.parseNewsId(backStackEntry),
            viewModel = hiltViewModel(),
            onBackPressed = { navController.navigateUp() },
            modifier = Modifier.fillMaxSize()
        )
    }

}