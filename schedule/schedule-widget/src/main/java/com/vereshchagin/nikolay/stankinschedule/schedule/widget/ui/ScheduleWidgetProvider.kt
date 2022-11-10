package com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.data.repository.ScheduleWidgetPreferenceImpl
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.repository.ScheduleWidgetPreference

class ScheduleWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val preference = widgetPreference(context)

        try {
            appWidgetIds.forEach { appWidgetId ->
                val data = preference.loadData(appWidgetId)
                ScheduleWidget.onUpdateWidget(context, appWidgetManager, appWidgetId, data)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val preference = widgetPreference(context)
        appWidgetIds.forEach { appWidgetId -> preference.deleteData(appWidgetId) }
    }

    // TODO("Не inject с помощью DI")
    private fun widgetPreference(context: Context): ScheduleWidgetPreference =
        ScheduleWidgetPreferenceImpl(context)
}