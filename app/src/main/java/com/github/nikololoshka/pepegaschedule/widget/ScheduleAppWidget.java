package com.github.nikololoshka.pepegaschedule.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.SplashActivity;

/**
 * Виджет с расписанием.
 */
public class ScheduleAppWidget extends AppWidgetProvider {

    /**
     * Обновляет данные на виджете с расписанием.
     * @param context контекст.
     * @param appWidgetManager менеджер виджетов.
     * @param appWidgetId ID виджета.
     */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        String schedule = ScheduleAppWidgetConfigureActivity.loadPref(context, appWidgetId);
        if (schedule == null) {
            schedule = context.getString(R.string.widget_schedule_name);
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_schedule_app);

        Intent appIntent = new Intent(context, SplashActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, appWidgetId, appIntent, 0);
        views.setTextViewText(R.id.widget_schedule_name, schedule);
        views.setOnClickPendingIntent(R.id.widget_schedule_name, appPendingIntent);

        Intent dataIntent = new Intent(context, ScheduleAppWidgetRemoteService.class);
        dataIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        Uri data = Uri.parse(dataIntent.toUri(Intent.URI_INTENT_SCHEME));
        dataIntent.setData(data);
        views.setRemoteAdapter(R.id.widget_schedule_list, dataIntent);

        // Обновление виджета
        appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_schedule_list);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Обновить все виджеты
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Удалить настройки , связанные с этими виджетами
        for (int appWidgetId : appWidgetIds) {
            ScheduleAppWidgetConfigureActivity.deletePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Вызывается когда установлен первый виджет
    }

    @Override
    public void onDisabled(Context context) {
        // Вызывается когда удален последний виджет
        // ApplicationPreference.clearScheduleWidgetIDs(context);
    }
}

