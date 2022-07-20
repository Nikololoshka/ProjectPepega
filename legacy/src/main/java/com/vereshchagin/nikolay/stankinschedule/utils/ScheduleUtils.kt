package com.vereshchagin.nikolay.stankinschedule.utils

import android.content.Context
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.*

/**
 * Вспомогательный класс для работы с расписанием.
 */
object ScheduleUtils {

    /**
     * Преобразует дату для отображения ее.
     */
    @JvmStatic
    fun dateToString(date: DateItem, context: Context): String {
        return when (date) {
            // одиночная дата
            is DateSingle -> {
                date.toString(DateTimeUtils.PRETTY_DATE_PATTERN)
            }
            // диапазон с датами
            is DateRange -> {
                val dateString = date.toString(DateTimeUtils.PRETTY_DATE_PATTERN, "-")
                val (every, through) = context.resources.getStringArray(R.array.frequency_simple_list)

                when (date.frequency()) {
                    Frequency.EVERY -> {
                        "$dateString $every"
                    }
                    Frequency.THROUGHOUT -> {
                        "$dateString $through"
                    }
                    else -> {
                        dateString
                    }
                }
            }
        }
    }

    fun subgroupToString(subgroup: Subgroup, context: Context): String {
        return context.resources.getStringArray(R.array.subgroup_simple_list).getOrElse(
            listOf(Subgroup.A, Subgroup.B).indexOf(subgroup)
        ) {
            ""
        }
    }
}