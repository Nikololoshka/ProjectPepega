package com.vereshchagin.nikolay.stankinschedule.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.navigation.entry.BottomNavEntry
import com.vereshchagin.nikolay.stankinschedule.news.review.ui.NewsReviewScreen
import com.vereshchagin.nikolay.stankinschedule.news.viewer.ui.NewsViewerActivity

object NewsNavEntry : BottomNavEntry(
    route = "news",
    nameRes = R.string.nav_news,
    iconRes = R.drawable.nav_news,
    hierarchy = listOf("news")
)

@Suppress("UNUSED_PARAMETER")
fun NavGraphBuilder.news(navController: NavController) {
    // Новости
    composable(route = NewsNavEntry.route) {
        val context = LocalContext.current

        NewsReviewScreen(
            viewModel = hiltViewModel(),
            navigateToViewer = { newsTitle, newsId ->
                context.startActivity(NewsViewerActivity.createIntent(context, newsTitle, newsId))
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}