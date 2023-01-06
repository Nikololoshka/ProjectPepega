package com.vereshchagin.nikolay.stankinschedule

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.vereshchagin.nikolay.stankinschedule.core.domain.settings.ApplicationPreference
import com.vereshchagin.nikolay.stankinschedule.core.domain.settings.DarkMode
import com.vereshchagin.nikolay.stankinschedule.core.ui.R
import com.vereshchagin.nikolay.stankinschedule.core.ui.notification.NotificationUtils
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Класс приложения Stankin Schedule.
 */
@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var applicationPreference: ApplicationPreference

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            // StrictMode.enableDefaults()
            Firebase.analytics.setAnalyticsCollectionEnabled(false)
            Firebase.crashlytics.setCrashlyticsCollectionEnabled(false)
        }

        updateDarkMode()
        setupNotifications()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    setMinimumLoggingLevel(android.util.Log.DEBUG)
                }
            }
            .setWorkerFactory(workerFactory)
            .build()
    }

    private fun updateDarkMode() {
        val mode = when (applicationPreference.currentDarkMode()) {
            DarkMode.Default -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            DarkMode.Dark -> AppCompatDelegate.MODE_NIGHT_YES
            DarkMode.Light -> AppCompatDelegate.MODE_NIGHT_NO
        }

        if (mode != AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.setDefaultNightMode(mode)
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
                getString(R.string.notification_common),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channelCommon.description = getString(R.string.notification_common_description)
            channelCommon.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channelCommon.enableVibration(true)
            channelCommon.enableLights(true)

            // модульного журнала
            val channelModuleJournal = NotificationChannel(
                NotificationUtils.CHANNEL_MODULE_JOURNAL,
                getString(R.string.notification_mj),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channelModuleJournal.description = getString(R.string.notification_mj_description)
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