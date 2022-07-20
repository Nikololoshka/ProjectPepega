package com.vereshchagin.nikolay.stankinschedule

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreferenceKt
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
    lateinit var preference: ApplicationPreferenceKt

    override fun onCreate() {
        super.onCreate()

        // включение сбора ошибок
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(
            !BuildConfig.DEBUG && preference.isCrashlyticsCollect
        )

        // включение сбора аналитики
        Firebase.analytics.setAnalyticsCollectionEnabled(
            preference.isAnalyticsCollect
        )

        updateDarkMode()
        singleton = this
    }

    /**
     * Устанавливает значение темной темы приложения исходя из настроек.
     */
    fun updateDarkMode() {
        when (preference.darkMode) {
            // по умолчанию
            ApplicationPreferenceKt.DARK_MODE_SYSTEM_DEFAULT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            // с учетом  режима энергосбережения
            ApplicationPreferenceKt.DARK_MODE_BATTERY_SAVER -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            }
            // ручное (приложение само контролирует)
            ApplicationPreferenceKt.DARK_MODE_MANUAL -> {
                AppCompatDelegate.setDefaultNightMode(
                    if (preference.isManualDarkModeEnabled) {
                        AppCompatDelegate.MODE_NIGHT_YES
                    } else {
                        AppCompatDelegate.MODE_NIGHT_NO
                    }
                )
            }
        }
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()


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