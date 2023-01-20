package com.vereshchagin.nikolay.stankinschedule.core.domain.settings

import org.joda.time.DateTime
import javax.inject.Inject

class ApplicationPreference @Inject constructor(
    private val manager: PreferenceRepository
) {

    var isMigrate_2_0: Boolean
        get() = manager.getBoolean(MIGRATE_2_0, false)
        set(value) = manager.saveBoolean(MIGRATE_2_0, value)

    var isAnalyticsEnabled: Boolean
        get() = manager.getBoolean(FIREBASE_ANALYTICS, true)
        set(value) = manager.saveBoolean(FIREBASE_ANALYTICS, value)

    var lastInAppUpdate: DateTime?
        get() = manager.getDateTime(LAST_IN_APP_UPDATE)
        set(value) {
            if (value != null) {
                manager.saveDateTime(LAST_IN_APP_UPDATE, value)
            }
        }

    fun currentDarkMode(): DarkMode {
        return DarkMode.from(manager.getString(DARK_MODE)) ?: DarkMode.Default
    }

    fun setDarkMode(mode: DarkMode) {
        manager.saveString(DARK_MODE, mode.tag)
    }

    companion object {
        private const val FIREBASE_ANALYTICS = "firebase_analytics"
        private const val DARK_MODE = "dark_mode_v2"
        private const val MIGRATE_2_0 = "migrate_2_0"
        private const val LAST_IN_APP_UPDATE = "last_in_app_update"
    }
}