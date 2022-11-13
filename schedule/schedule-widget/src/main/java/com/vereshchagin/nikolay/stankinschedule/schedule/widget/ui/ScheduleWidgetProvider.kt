package com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.data.repository.ScheduleWidgetPreferenceImpl
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.repository.ScheduleWidgetPreference
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.utils.ScheduleDeepLink

class ScheduleWidgetProvider : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val action = intent.action
        if (!action.isNullOrEmpty() && action == ScheduleDeepLink.SCHEDULE_VIEWER_ACTION) {
            Log.d("ScheduleWidgetProvider", "onReceive: ${intent.data}")
        }

    }

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