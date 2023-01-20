package com.vereshchagin.nikolay.stankinschedule.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.staticCompositionLocalOf
import com.vereshchagin.nikolay.stankinschedule.core.domain.repository.ConsoleLoggerAnalytics
import com.vereshchagin.nikolay.stankinschedule.core.domain.repository.LoggerAnalytics


val LocalAnalytics = staticCompositionLocalOf<LoggerAnalytics> {
    ConsoleLoggerAnalytics()
}

@Composable
fun TrackCurrentScreen(screen: String) {
    val analytics = LocalAnalytics.current
    DisposableEffect(Unit) {
        analytics.logEvent(LoggerAnalytics.SCREEN_ENTER, screen)
        onDispose {
            analytics.logEvent(LoggerAnalytics.SCREEN_LEAVE, screen)
        }
    }
}