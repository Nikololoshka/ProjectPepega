package com.vereshchagin.nikolay.stankinschedule.settings

import android.content.Context
import androidx.preference.PreferenceManager
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup
import dagger.hilt.android.qualifiers.ApplicationContext
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * Класс-обертка для доступа к настройкам приложения.
 *
 * @param context контекст приложения.
 */
class ApplicationPreferenceKt @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    val scheduleDelta
        get() = preferences.getInt(SCHEDULE_DELTA, 2).coerceAtLeast(2)

    var isSubgroupDisplay
        get() = preferences.getBoolean(DISPLAY_SUBGROUP, true)
        set(value) = preferences.edit()
            .putBoolean(DISPLAY_SUBGROUP, value)
            .apply()

    var scheduleSubgroup
        get() = Subgroup.of(preferences.getString(SCHEDULE_SUBGROUP, Subgroup.COMMON.tag)!!)
        set(value) = preferences.edit()
            .putString(SCHEDULE_SUBGROUP, value.tag)
            .apply()

    fun colorPreferenceChanged(callback: (key: String) -> Unit) {
        preferences.registerOnSharedPreferenceChangeListener { _, key ->
            if (key.startsWith("schedule_") && key.endsWith("_color")) {
                callback(key)
            }
        }
    }

    /**
     * Режим темной темы.
     */
    var darkMode
        get() = preferences.getString(DARK_MODE, DARK_MODE_SYSTEM_DEFAULT)!!
        set(value) =
            preferences.edit()
                .putString(DARK_MODE, value)
                .apply()

    /**
     * Как должно отображаться расписание.
     */
    val scheduleViewMethod
        get() = preferences.getString(SCHEDULE_VIEW_METHOD, SCHEDULE_VIEW_HORIZONTAL)

    /**
     * Текущие значение ручного переключателя темной темы.
     */
    var isManualDarkModeEnabled
        get() = preferences.getBoolean(MANUAL_MODE, false)
        set(value) {
            preferences.edit()
                .putBoolean(MANUAL_MODE, value)
                .apply()
        }

    /**
     * Время, когда последний раз проверялось доступность обновлений.
     */
    var updateAppTime: DateTime?
        get() {
            return preferences.getString(UPDATE_APP_TIME, null)?.let { dateTime ->
                DateTime.parse(dateTime)
            }
        }
        set(value) {
            if (value != null) {
                preferences.edit()
                    .putString(UPDATE_APP_TIME, value.toString())
                    .apply()
            }
        }

    /**
     * Можно ли использовать Firebase аналитику для
     * сбора данных об использовании приложения.
     */
    val isAnalyticsCollect
        get() = preferences.getBoolean(FIREBASE_ANALYTICS, true)

    /**
     * Можно ли использовать Firebase crashlytics для
     * сбора ошибок об использовании приложения.
     */
    val isCrashlyticsCollect
        get() = preferences.getBoolean(FIREBASE_CRASHLYTICS, true)

    companion object {
        private const val UPDATE_APP_TIME = "update_app_time"

        private const val SCHEDULE_DELTA = "home_schedule_delta"
        private const val DISPLAY_SUBGROUP = "schedule_home_subgroup"
        private const val SCHEDULE_SUBGROUP = "schedule_subgroup"

        const val LECTURE_COLOR = "schedule_lecture_color"
        const val SEMINAR_COLOR = "schedule_seminar_color"
        const val LABORATORY_COLOR = "schedule_laboratory_color"
        const val SUBGROUP_A_COLOR = "schedule_subgroup_a_color"
        const val SUBGROUP_B_COLOR = "schedule_subgroup_b_color"

        const val DARK_MODE_SYSTEM_DEFAULT = "pref_system_default"
        const val DARK_MODE_BATTERY_SAVER = "pref_battery_saver"
        const val DARK_MODE_MANUAL = "pref_manual_mode"

        private const val APP_BROWSER = "app_browser"

        const val SCHEDULE_VIEW_VERTICAL = "pref_vertical"
        const val SCHEDULE_VIEW_HORIZONTAL = "pref_horizontal"

        private const val FIREBASE_ANALYTICS = "firebase_analytics"
        private const val FIREBASE_CRASHLYTICS = "firebase_crashlytics"

        private const val SCHEDULE_VIEW_METHOD = "schedule_view_method"
        private const val SCHEDULE_LIMIT = "schedule_view_limit"

        private const val DARK_MODE = "dark_mode"
        private const val MANUAL_MODE = "manual_mode"
    }
}