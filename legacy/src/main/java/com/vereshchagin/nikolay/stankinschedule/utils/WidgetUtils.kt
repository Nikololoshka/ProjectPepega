package com.vereshchagin.nikolay.stankinschedule.utils

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.widget.ScheduleWidget
import com.vereshchagin.nikolay.stankinschedule.widget.ScheduleWidgetConfigureActivity

/**
 * Вспомогательные функции по работе с виджетами.
 */
object WidgetUtils {

    /**
     * Возвращает текущий список виджетов с расписаниями.
     */
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
     * Обновляет целиком виджет для расписания, если такой имеется.
     */
    suspend fun updateScheduleWidget(
        context: Context,
        scheduleId: Long,
        repository: ScheduleRepository,
    ) {
        scheduleWidgets(context).forEach { widgetId ->
            val data = ScheduleWidgetConfigureActivity.loadPref(context, widgetId)
            if (data.scheduleId == scheduleId) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                ScheduleWidget.updateScheduleWidget(
                    context, appWidgetManager, widgetId, scheduleId, repository
                )
                return
            }
        }
    }

    /**
     * Обновляет список в виджете с расписанием, если такой имеется.
     */
    fun updateScheduleWidgetList(
        context: Context,
        scheduleId: Long,
    ) {
        scheduleWidgets(context).forEach { widgetId ->
            val data = ScheduleWidgetConfigureActivity.loadPref(context, widgetId)
            if (data.scheduleId == scheduleId) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                ScheduleWidget.updateScheduleWidgetList(appWidgetManager, widgetId)
                return
            }
        }
    }
}