package com.vereshchagin.nikolay.stankinschedule.utils

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import com.vereshchagin.nikolay.stankinschedule.widget.ScheduleWidget
import com.vereshchagin.nikolay.stankinschedule.widget.ScheduleWidgetConfigureActivity

/**
 * Вспомогательные функции по работе с виджетами.
 */
object WidgetUtils {

    /**
     * Возвращает текущий список виджетов с расписаниями.
     */
    @JvmStatic
    fun scheduleWidgets(context: Context): List<Int> {
        val widgetManager = AppWidgetManager.getInstance(context)
        val ids = widgetManager.getAppWidgetIds(
            ComponentName(
                context,
                ScheduleWidget::class.java
            )
        )
        return ids.toList()
    }

    /**
     * Обновляет все виджеты с расписанием.
     */
    @JvmStatic
    fun updateAllScheduleWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        scheduleWidgets(context).forEach {
            ScheduleWidget.updateAppWidget(context, appWidgetManager, it)
        }
    }

    /**
     * Обновляет виджет для расписания, если такой имеется.
     */
    @JvmStatic
    fun updateScheduleWidget(context: Context, scheduleId: Long) {
        scheduleWidgets(context).forEach {
            val data = ScheduleWidgetConfigureActivity.loadPref(context, it)
            if (data.scheduleName == scheduleName) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                ScheduleWidget.updateAppWidget(context, appWidgetManager, it)
                return // из функции
            }
        }
    }
}