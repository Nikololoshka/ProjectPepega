package com.vereshchagin.nikolay.stankinschedule

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.vereshchagin.nikolay.stankinschedule.core.data.notification.NotificationUtils
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import com.vereshchagin.nikolay.stankinschedule.ui.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import com.vereshchagin.nikolay.stankinschedule.core.ui.R as R_core


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R_core.style.AppTheme)

        super.onCreate(savedInstanceState)
        setupNotifications()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                MainScreen()
            }
        }
    }

    /**
     * Устанавливает настройки уведомлений для приложения.
     */
    private fun setupNotifications() {
        // android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // общего назначения
            val channelCommon = NotificationChannel(
                NotificationUtils.CHANNEL_COMMON,
                getString(R_core.string.notification_common),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channelCommon.description = getString(R_core.string.notification_common_description)
            channelCommon.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channelCommon.enableVibration(true)
            channelCommon.enableLights(true)

            // модульного журнала
            val channelModuleJournal = NotificationChannel(
                NotificationUtils.CHANNEL_MODULE_JOURNAL,
                getString(R_core.string.notification_mj),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channelModuleJournal.description = getString(R_core.string.notification_mj_description)
            channelModuleJournal.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channelModuleJournal.enableVibration(true)
            channelModuleJournal.enableLights(true)

            getSystemService(NotificationManager::class.java)?.let { manager ->
                manager.createNotificationChannel(channelCommon)
                manager.createNotificationChannel(channelModuleJournal)
            }
        }
    }

}