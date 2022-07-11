package com.vereshchagin.nikolay.stankinschedule.news.ui.screen

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.ImageLoader
import com.vereshchagin.nikolay.stankinschedule.news.ui.components.NewsPostColumn
import com.vereshchagin.nikolay.stankinschedule.news.ui.components.defaultImageLoader


@Composable
fun NewsSubdivisionScreen(
    newsSubdivision: Int,
    navController: NavController,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader = defaultImageLoader(LocalContext.current),
    viewModel: NewsSubdivisionViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    NewsPostColumn(
        posts = viewModel.news(newsSubdivision),
        onClick = { post ->
            Toast.makeText(context, "ID: ${post.id}", Toast.LENGTH_SHORT).show()
            navController.navigate("viewer/${post.id}")
        },
        imageLoader = imageLoader,
        modifier = modifier
    )
}