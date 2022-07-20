package com.vereshchagin.nikolay.stankinschedule.widget

import android.content.Context
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup
import com.vereshchagin.nikolay.stankinschedule.utils.ScheduleUtils

/**
 * Информация с расписанием для виджета.
 */
class ScheduleWidgetData(
    val scheduleName: String,
    val scheduleId: Long,
    val subgroup: Subgroup,
    val display: Boolean,
) {
    /**
     * Возвращает название расписания для отображения на виджете.
     */
    fun displayName(context: Context): String {
        if (scheduleName.isEmpty()) {
            return context.getString(R.string.widget_schedule_name)
        }

        // подгруппа виджета
        if (display && subgroup != Subgroup.COMMON) {
            return "$scheduleName ${ScheduleUtils.subgroupToString(subgroup, context)}"
        }

        return scheduleName
    }
}