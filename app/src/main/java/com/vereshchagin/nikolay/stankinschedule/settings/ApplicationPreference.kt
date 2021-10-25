package com.vereshchagin.nikolay.stankinschedule.settings

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.vereshchagin.nikolay.stankinschedule.R

/**
 * Класс-обертка для доступа к настройкам приложения.
 */
object ApplicationPreference {

    private const val UPDATE_APP_TIME = "update_app_time"

    private const val HOME_SCHEDULE_DELTA = "home_schedule_delta"
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

    // private const val FIRST_RUN = "first_run_v2"
    private const val FIREBASE_ANALYTICS = "firebase_analytics"
    private const val FIREBASE_CRASHLYTICS = "firebase_crashlytics"

    private const val SCHEDULE_VIEW_METHOD = "schedule_view_method"
    private const val SCHEDULE_LIMIT = "schedule_view_limit"

    private const val DARK_MODE = "dark_mode"
    private const val MANUAL_MODE = "manual_mode"

    /**
     * Возвращает значение, должен ли использоваться встроенный браузер.
     */
    @JvmStatic
    fun useAppBrowser(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(APP_BROWSER, true)
    }

    /**
     * Вычисляет цвет для пары.
     */
    @JvmStatic
    fun pairColor(context: Context, preferenceName: String): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getInt(preferenceName, defaultColor(context, preferenceName))
    }

    /**
     * Возвращает список цветов для расписания.
     */
    @JvmStatic
    fun colors(context: Context, vararg colorNames: String): List<Int> {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val colors = ArrayList<Int>()

        for (colorName in colorNames) {
            colors += preferences.getInt(colorName, defaultColor(context, colorName))
        }

        return colors
    }

    /**
     * Возвращает цвет по умолчанию для расписания.
     */
    @JvmStatic
    fun defaultColor(context: Context, colorName: String) = ContextCompat.getColor(
        context, when (colorName) {
            LECTURE_COLOR -> R.color.colorCardLecture
            SEMINAR_COLOR -> R.color.colorCardSeminar
            LABORATORY_COLOR -> R.color.colorCardLaboratory
            SUBGROUP_A_COLOR -> R.color.colorCardSubgroupA
            SUBGROUP_B_COLOR -> R.color.colorCardSubgroupB
            else -> {
                throw IllegalArgumentException("Unknown color name $colorName")
            }
        }
    )
}