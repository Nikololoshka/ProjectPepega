package com.vereshchagin.nikolay.stankinschedule

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import com.vereshchagin.nikolay.stankinschedule.core.domain.logger.LoggerAnalytics
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.LocalAnalytics
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import com.vereshchagin.nikolay.stankinschedule.ui.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.vereshchagin.nikolay.stankinschedule.core.ui.R as R_core


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var loggerAnalytics: LoggerAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R_core.style.AppTheme)
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                CompositionLocalProvider(LocalAnalytics provides loggerAnalytics) {
                    MainScreen()
                }
            }
        }
    }
}