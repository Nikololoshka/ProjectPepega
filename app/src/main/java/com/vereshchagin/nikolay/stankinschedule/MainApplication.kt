package com.vereshchagin.nikolay.stankinschedule

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.vereshchagin.nikolay.stankinschedule.core.settings.ApplicationPreference
import com.vereshchagin.nikolay.stankinschedule.core.settings.DarkMode
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

        updateDarkMode()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
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
}