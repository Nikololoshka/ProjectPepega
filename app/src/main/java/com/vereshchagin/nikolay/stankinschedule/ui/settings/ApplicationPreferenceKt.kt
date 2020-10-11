package com.vereshchagin.nikolay.stankinschedule.ui.settings

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.vereshchagin.nikolay.stankinschedule.R

/**
 * Настройки приложения.
 */
object ApplicationPreferenceKt {

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