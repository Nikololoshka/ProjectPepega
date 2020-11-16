package com.vereshchagin.nikolay.stankinschedule.ui.settings

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.model.home.HomeScheduleSettings
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup
import org.joda.time.DateTime

/**
 * Настройки приложения.
 */
object ApplicationPreferenceKt {

    private const val UPDATE_APP_TIME = "update_app_time"

    private const val HOME_SCHEDULE_DELTA = "home_schedule_delta"
    private const val DISPLAY_SUBGROUP = "schedule_home_subgroup"
    private const val SCHEDULE_SUBGROUP = "schedule_subgroup"

    private const val FIREBASE_ANALYSTIC = "firebase_analytics"

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
    fun homeScheduleSettings(context: Context): HomeScheduleSettings {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return HomeScheduleSettings(
            preferences.getInt(HOME_SCHEDULE_DELTA, 2),
            preferences.getBoolean(DISPLAY_SUBGROUP, true),
            Subgroup.of(preferences.getString(SCHEDULE_SUBGROUP, Subgroup.COMMON.tag)!!),
            SchedulePreference.favorite(context)
            // TODO("07/11/20 переместить реализация избранного в настройки приложения")
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
     * Возвращает true если можно ли использовать Firebase аналитику для
     * сбора данных об использовании приложения. Иначе false.
     */
    @JvmStatic
    fun firebaseAnalytics(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(FIREBASE_ANALYSTIC, true)
    }

    /**
     * Возвращает цвет по умолчанию для расписания.
     */
    private fun defaultColor(context: Context, colorName: String) = ContextCompat.getColor(
        context, when (colorName) {
            ApplicationPreference.LECTURE_COLOR -> R.color.colorCardLecture
            ApplicationPreference.SEMINAR_COLOR -> R.color.colorCardSeminar
            ApplicationPreference.LABORATORY_COLOR -> R.color.colorCardLaboratory
            ApplicationPreference.SUBGROUP_A_COLOR -> R.color.colorCardSubgroupA
            ApplicationPreference.SUBGROUP_B_COLOR -> R.color.colorCardSubgroupB
            else -> {
                throw IllegalArgumentException("Unknown color name $colorName")
            }
        }
    )
}