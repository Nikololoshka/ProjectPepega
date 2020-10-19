package com.vereshchagin.nikolay.stankinschedule.ui.settings

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.vereshchagin.nikolay.stankinschedule.R
import org.joda.time.DateTime

/**
 * Настройки приложения.
 */
object ApplicationPreferenceKt {

    private const val UPDATE_APP_TIME = "update_app_time"

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