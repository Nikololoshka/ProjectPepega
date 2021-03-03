package com.vereshchagin.nikolay.stankinschedule.settings

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.model.home.HomeScheduleSettings
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup.Companion.of
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import org.joda.time.DateTime

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
     * Возвращает текущие сохранённое значение режима темной темы.
     */
    @JvmStatic
    fun currentDarkMode(context: Context): String? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(DARK_MODE, DARK_MODE_SYSTEM_DEFAULT)
    }

    /**
     * Устанавливает значение режима темной темы.
     */
    @JvmStatic
    fun setDarkMode(context: Context, darkMode: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit()
            .putString(DARK_MODE, darkMode)
            .apply()
    }

    /**
     * Возвращает текущие значение ручного переключателя темной темы.
     */
    @JvmStatic
    fun currentManualMode(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(MANUAL_MODE, false)
    }

    /**
     * Устанавливает значение ручного переключателя темной темы.
     */
    @JvmStatic
    fun setManualMode(context: Context, isDarkModeEnabled: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit()
            .putBoolean(MANUAL_MODE, isDarkModeEnabled)
            .apply()
    }

    /**
     * Возвращает значение, должен ли использоваться встроенный браузер.
     */
    @JvmStatic
    fun useAppBrowser(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(APP_BROWSER, true)
    }

    /**
     * Возвращает значение, как должно отображаться расписание.
     */
    @JvmStatic
    fun scheduleViewMethod(context: Context): String? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(SCHEDULE_VIEW_METHOD, SCHEDULE_VIEW_HORIZONTAL)
    }

    /**
     * Возвращает значение, должно ли ограничиваться расписание.
     */
    @JvmStatic
    fun scheduleLimit(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(SCHEDULE_LIMIT, false)
    }

    /**
     * Возвращает true, если необходимо отображать подгруппу на главной.
     */
    @JvmStatic
    fun displaySubgroup(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(DISPLAY_SUBGROUP, true)
    }

    /**
     * Устанавливает, нужно ли отображать подгруппу на главной.
     */
    @JvmStatic
    fun setDisplaySubgroup(context: Context, display: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit()
            .putBoolean(DISPLAY_SUBGROUP, display)
            .apply()
    }

    /**
     * Возвращает подгруппу.
     */
    @JvmStatic
    fun subgroup(context: Context): Subgroup {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return of(preferences.getString(SCHEDULE_SUBGROUP, Subgroup.COMMON.tag)!!)
    }

    /**
     * Устанавливает подгруппу.
     */
    @JvmStatic
    fun setSubgroup(context: Context, subgroup: Subgroup) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit()
            .putString(SCHEDULE_SUBGROUP, subgroup.tag)
            .apply()
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
     * Возвращает время, когда последний раз проверялось доступность обновлений.
     * Возвращается null, если никогда не проверялось.
     */
    @JvmStatic
    fun updateAppTime(context: Context): DateTime? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val dateTime = preferences.getString(UPDATE_APP_TIME, null) ?: return null
        return DateTime.parse(dateTime)
    }

    /**
     * Возвращает настройки отображения расписания на главной странице.
     */
    @JvmStatic
    fun homeScheduleSettings(context: Context): HomeScheduleSettings {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return HomeScheduleSettings(
            preferences.getInt(HOME_SCHEDULE_DELTA, 2),
            preferences.getBoolean(DISPLAY_SUBGROUP, true),
            of(preferences.getString(SCHEDULE_SUBGROUP, Subgroup.COMMON.tag)!!),
            ScheduleRepository.favorite(context)
        )
    }

    /**
     * Устанавливает время последней проверки обновления приложения.
     */
    @JvmStatic
    fun setUpdateAppTime(context: Context, dateTime: DateTime) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit()
            .putString(UPDATE_APP_TIME, dateTime.toString())
            .apply()
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

    /**
     * Возвращает true если можно ли использовать Firebase аналитику для
     * сбора данных об использовании приложения. Иначе false.
     */
    @JvmStatic
    fun firebaseAnalytics(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(FIREBASE_ANALYTICS, true)
    }

    /**
     * Возвращает true если можно ли использовать Firebase crashlytics для
     * сбора ошибок об использовании приложения. Иначе false.
     */
    @JvmStatic
    fun firebaseCrashlytics(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(FIREBASE_CRASHLYTICS, true)
    }
}