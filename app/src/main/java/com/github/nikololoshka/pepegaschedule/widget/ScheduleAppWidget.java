package com.github.nikololoshka.pepegaschedule.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import androidx.navigation.NavDeepLinkBuilder;

import com.github.nikololoshka.pepegaschedule.MainActivity;
import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.schedule.view.ScheduleViewFragment;
import com.github.nikololoshka.pepegaschedule.settings.SchedulePreference;

/**
 * Виджет с расписанием.
 */
public class ScheduleAppWidget extends AppWidgetProvider {

    public static final String SCHEDULE_DAY_TIME = "widget_schedule_day_time";
    public static final String SCHEDULE_NAME = "widget_schedule_name";

    private static final String ACTION_SCHEDULE_DAY_CLICKED = "action_schedule_day_clicked";

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

        // для открытия приложения на распиании
        Bundle scheduleBundle = new Bundle();
        scheduleBundle.putString(ScheduleViewFragment.ARG_SCHEDULE_NAME, schedule);
        scheduleBundle.putString(ScheduleViewFragment.ARG_SCHEDULE_PATH,
                SchedulePreference.createPath(context, schedule));

        PendingIntent schedulePendingIntent = new NavDeepLinkBuilder(context)
                .setComponentName(MainActivity.class)
                .setGraph(R.navigation.activity_main_nav_graph)
                .setDestination(R.id.nav_schedule_view_fragment)
                .setArguments(scheduleBundle)
                .createPendingIntent();

        views.setTextViewText(R.id.widget_schedule_name, schedule);
        views.setOnClickPendingIntent(R.id.widget_schedule_name, schedulePendingIntent);

        // для открытия приложения на распиании на определенном дне
        Intent scheduleDayIntent = new Intent(context, ScheduleAppWidget.class);
        scheduleDayIntent.setAction(ACTION_SCHEDULE_DAY_CLICKED);
        PendingIntent scheduleDayPendingIntent = PendingIntent
                .getBroadcast(context, appWidgetId, scheduleDayIntent, 0);

        views.setPendingIntentTemplate(R.id.widget_schedule_list, scheduleDayPendingIntent);

        // установка адаптера
        Intent dataIntent = new Intent(context, ScheduleAppWidgetRemoteService.class);
        dataIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        // уникальный адаптер для каждого виджета
        Uri data = Uri.parse(dataIntent.toUri(Intent.URI_INTENT_SCHEME));
        dataIntent.setData(data);

        views.setRemoteAdapter(R.id.widget_schedule_list, dataIntent);

        // Обновление виджета
        appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_schedule_list);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();
        if (action != null && action.equalsIgnoreCase(ACTION_SCHEDULE_DAY_CLICKED)) {
            long time = intent.getLongExtra(SCHEDULE_DAY_TIME, -1);
            String scheduleName =  intent.getStringExtra(SCHEDULE_NAME);

            if (scheduleName == null) {
                return;
            }

            // создание intent'а на открытие расписание на определенном дне
            Bundle scheduleDayBundle = new Bundle();
            scheduleDayBundle.putString(ScheduleViewFragment.ARG_SCHEDULE_NAME, scheduleName);
            scheduleDayBundle.putString(ScheduleViewFragment.ARG_SCHEDULE_PATH,
                    SchedulePreference.createPath(context, scheduleName));
            scheduleDayBundle.putLong(ScheduleViewFragment.ARG_SCHEDULE_DAY, time);

            PendingIntent scheduleDayPendingIntent = new NavDeepLinkBuilder(context)
                    .setComponentName(MainActivity.class)
                    .setGraph(R.navigation.activity_main_nav_graph)
                    .setDestination(R.id.nav_schedule_view_fragment)
                    .setArguments(scheduleDayBundle)
                    .createPendingIntent();

            try {
                scheduleDayPendingIntent.send();
            } catch (PendingIntent.CanceledException ignored) {

            }
        }
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

