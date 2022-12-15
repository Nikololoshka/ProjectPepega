package com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Subgroup
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.model.ScheduleWidgetData
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.repository.ScheduleWidgetPreference
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui.configure.ScheduleWidgetConfigureActivity
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui.utils.ScheduleDeepLink
import com.vereshchagin.nikolay.stankinschedule.widget.data.repository.ScheduleWidgetPreferenceImpl
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.R as R_core

object ScheduleWidget {

    fun widgetPreference(context: Context): ScheduleWidgetPreference =
        ScheduleWidgetPreferenceImpl(context)

    fun updateWidgetById(context: Context, scheduleId: Long, fullUpdate: Boolean) {
        val widgetManager = AppWidgetManager.getInstance(context)
        val preference = widgetPreference(context)

        val ids = allScheduleWidgets(context, widgetManager)
        for (id in ids) {
            val data = preference.loadData(id)
            if (data != null && data.scheduleId == scheduleId) {
                onUpdateWidget(context, widgetManager, id, data, fullUpdate)
                return
            }
        }
    }

    fun updateAllWidgets(context: Context, fullUpdate: Boolean) {
        val widgetManager = AppWidgetManager.getInstance(context)
        val preference = widgetPreference(context)

        val ids = allScheduleWidgets(context, widgetManager)
        for (id in ids) {
            val data = preference.loadData(id)
            if (data != null) {
                onUpdateWidget(context, widgetManager, id, data, fullUpdate)
            }
        }
    }

    private fun allScheduleWidgets(context: Context, manager: AppWidgetManager): List<Int> {
        val componentName = ComponentName(context, ScheduleWidgetProvider::class.java)
        return manager.getAppWidgetIds(componentName).toList()
    }

    fun onUpdateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        appWidgetData: ScheduleWidgetData?,
        fullUpdate: Boolean
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_schedule)

        // Обработка отсутствия данных
        if (appWidgetData == null) {
            appWidgetManager.updateAppWidget(appWidgetId, views)
            return
        }

        // установка имени
        val scheduleName = displayScheduleName(context, appWidgetData)
        views.setTextViewText(R.id.widget_schedule_name, scheduleName)
        val scheduleNameIntent = scheduleNameIntent(context, appWidgetData.scheduleId)
        views.setOnClickPendingIntent(R.id.widget_schedule_name, scheduleNameIntent)

        // конфигуратор
        val configureIntent = configureIntent(context, appWidgetId)
        views.setOnClickPendingIntent(R.id.widget_settings, configureIntent)

        // для открытия приложения на расписании на определенном дне
        val dayPendingIntent = dayPendingIntent(context, appWidgetId)
        views.setPendingIntentTemplate(R.id.widget_days, dayPendingIntent)

        // установка адаптера
        val dataIntent = remoteAdapterIntent(context, appWidgetId)
        views.setRemoteAdapter(R.id.widget_days, dataIntent)

        // Обновление виджета
        appWidgetManager.updateAppWidget(appWidgetId, views)

        if (fullUpdate) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_days)
        }
    }

    private fun dayPendingIntent(context: Context, appWidgetId: Int): PendingIntent {
        return PendingIntent.getActivity(
            context,
            appWidgetId,
            Intent(ScheduleDeepLink.SCHEDULE_VIEWER_ACTION),
            PendingIntent.FLAG_UPDATE_CURRENT or FLAG_MUTABLE_COMPAT
        )
    }

    private val FLAG_MUTABLE_COMPAT: Int
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0

    private fun displayScheduleName(context: Context, data: ScheduleWidgetData): String {
        if (!data.display || data.subgroup == Subgroup.COMMON) return data.scheduleName

        val postfix = when (data.subgroup) {
            Subgroup.A -> context.getString(R_core.string.subgroup_a)
            Subgroup.B -> context.getString(R_core.string.subgroup_b)
            else -> "" // невозможно, т.к. выше сравнение с Subgroup.COMMON
        }

        return data.scheduleName + " " + postfix
    }


    private fun remoteAdapterIntent(context: Context, appWidgetId: Int): Intent {
        val dataIntent = Intent(context, ScheduleWidgetService::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }

        // уникальный адаптер для каждого виджета
        val data = Uri.parse(dataIntent.toUri(Intent.URI_INTENT_SCHEME))
        dataIntent.data = data

        return dataIntent
    }

    private fun configureIntent(context: Context, appWidgetId: Int): PendingIntent {
        return PendingIntent.getActivity(
            context,
            0,
            Intent(context, ScheduleWidgetConfigureActivity::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun scheduleNameIntent(context: Context, scheduleId: Long): PendingIntent {
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(ScheduleDeepLink.viewerIntent(scheduleId))
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
    }
}