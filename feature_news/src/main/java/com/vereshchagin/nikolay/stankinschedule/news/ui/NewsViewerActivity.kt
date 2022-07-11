package com.vereshchagin.nikolay.stankinschedule.news.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.vereshchagin.nikolay.stankinschedule.news.ui.screen.NewsViewerScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsViewerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val newsId = intent.getIntExtra(EXTRA_NEWS_ID, -1)
        val newsTitle = intent.getStringExtra(EXTRA_NEWS_TITLE) ?: "News viewer"

        if (newsId <= -1) {
            Toast.makeText(this, "Invalid news id: $newsId", Toast.LENGTH_SHORT).show()
            onBackPressed()
            return
        }

        setContent {
            MaterialTheme {
                TopAppBar(
                    title = { Text(text = newsTitle) },
                    navigationIcon = {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                )
                NewsViewerScreen(
                    newsId = newsId,
                    viewModel = hiltViewModel(),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    companion object {
        private const val EXTRA_NEWS_ID = "news_id"
        private const val EXTRA_NEWS_TITLE = "extra_news_title"
    }
}