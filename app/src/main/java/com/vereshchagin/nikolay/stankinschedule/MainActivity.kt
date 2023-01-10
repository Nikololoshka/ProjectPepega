package com.vereshchagin.nikolay.stankinschedule

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.vereshchagin.nikolay.stankinschedule.core.domain.logger.LoggerAnalytics
import com.vereshchagin.nikolay.stankinschedule.core.domain.settings.ApplicationPreference
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.LocalAnalytics
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import com.vereshchagin.nikolay.stankinschedule.migration.Migrator
import com.vereshchagin.nikolay.stankinschedule.ui.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.vereshchagin.nikolay.stankinschedule.core.ui.R as R_core


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var migrator: dagger.Lazy<Migrator>

    @Inject
    lateinit var appPreference: ApplicationPreference

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

        lifecycleScope.launchWhenCreated {
            if (appPreference.isMigrate_2_0) {
                withContext(Dispatchers.IO) {
                    migrator.get().migrate_2_0_0()
                }
                appPreference.isMigrate_2_0 = true
            }
        }
    }
}