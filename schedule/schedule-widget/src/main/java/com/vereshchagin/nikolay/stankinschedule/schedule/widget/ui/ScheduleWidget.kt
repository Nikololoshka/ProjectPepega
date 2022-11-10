package com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.R
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.model.ScheduleWidgetData
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.utils.ScheduleDeepLink

object ScheduleWidget {

    fun onUpdateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        appWidgetData: ScheduleWidgetData?
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_schedule)

        // Обработка отсутствия данных
        if (appWidgetData == null) {
            appWidgetManager.updateAppWidget(appWidgetId, views)
            return
        }

        // установка имени
        views.setTextViewText(
            R.id.widget_schedule_name, appWidgetData.scheduleName
        )
        views.setOnClickPendingIntent(
            R.id.widget_schedule_name, scheduleNameIntent(context, appWidgetData.scheduleId)
        )

        //
        val pending = PendingIntent.getActivity(
            context,
            0,
            Intent(context, ScheduleWidgetConfigureActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_settings, pending)

        // установка адаптера
        val dataIntent = Intent(context, ScheduleWidgetService::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }

        // уникальный адаптер для каждого виджета
        val data = Uri.parse(dataIntent.toUri(Intent.URI_INTENT_SCHEME))
        dataIntent.data = data

        views.setRemoteAdapter(R.id.widget_days, dataIntent)

        // Обновление виджета
        appWidgetManager.updateAppWidget(appWidgetId, views)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_days)
    }

    private fun scheduleNameIntent(context: Context, scheduleId: Long): PendingIntent {
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(ScheduleDeepLink.viewerIntent(scheduleId))
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
    }
}