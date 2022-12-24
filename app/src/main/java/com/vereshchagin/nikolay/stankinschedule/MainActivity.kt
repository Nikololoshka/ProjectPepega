package com.vereshchagin.nikolay.stankinschedule

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import com.vereshchagin.nikolay.stankinschedule.ui.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import com.vereshchagin.nikolay.stankinschedule.core.ui.R as R_core


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R_core.style.AppTheme)
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                MainScreen()
            }
        }
    }
}