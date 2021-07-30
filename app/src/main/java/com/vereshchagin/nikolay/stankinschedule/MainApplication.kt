package com.vereshchagin.nikolay.stankinschedule

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference
import dagger.hilt.android.HiltAndroidApp

/**
 * Класс приложения Stankin Schedule.
 */
@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // включение сбора ошибок
        val isCrashlytics = ApplicationPreference.firebaseCrashlytics(this)
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG && isCrashlytics)

        // включение сбора аналитики
        val isAnalytics = ApplicationPreference.firebaseAnalytics(this)
        Firebase.analytics.setAnalyticsCollectionEnabled(isAnalytics)

        updateDarkMode()
        singleton = this
    }

    /**
     * Устанавливает значение темной темы приложения исходя из настроек.
     */
    fun updateDarkMode() {
        when (ApplicationPreference.currentDarkMode(this)) {
            // по умолчанию
            ApplicationPreference.DARK_MODE_SYSTEM_DEFAULT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            // с учетом  режима энергосбережения
            ApplicationPreference.DARK_MODE_BATTERY_SAVER -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            }
            // ручное (приложение само контролирует)
            ApplicationPreference.DARK_MODE_MANUAL -> {
                val isDark = ApplicationPreference.currentManualMode(this)
                AppCompatDelegate.setDefaultNightMode(
                    if (isDark) {
                        AppCompatDelegate.MODE_NIGHT_YES
                    } else {
                        AppCompatDelegate.MODE_NIGHT_NO
                    }
                )
            }
        }
    }

    companion object {
        /**
         * Объект приложения.
         */
        private var singleton: MainApplication? = null

        @JvmStatic
        val instance: MainApplication
            get() {
                if (singleton == null) {
                    singleton = MainApplication()
                }
                return singleton!!
            }
    }
}