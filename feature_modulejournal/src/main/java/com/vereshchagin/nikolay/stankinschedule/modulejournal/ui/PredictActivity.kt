package com.vereshchagin.nikolay.stankinschedule.modulejournal.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.AppScaffold
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.screen.PredictScreen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PredictActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                AppScaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Column {
                                    Text(text = "Title")
                                    Text(text = "Semester",
                                        style = MaterialTheme.typography.caption)
                                }
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        onBackPressed()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = null
                                    )
                                }
                            },
                        )
                    }
                ) { innerPadding ->
                    PredictScreen(
                        viewModel = hiltViewModel(),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}