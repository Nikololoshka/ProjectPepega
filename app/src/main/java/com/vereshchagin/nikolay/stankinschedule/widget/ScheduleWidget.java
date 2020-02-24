package com.vereshchagin.nikolay.stankinschedule.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import androidx.navigation.NavDeepLinkBuilder;

import com.vereshchagin.nikolay.stankinschedule.MainActivity;
import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.schedule.view.ScheduleViewFragment;
import com.vereshchagin.nikolay.stankinschedule.settings.SchedulePreference;

import java.util.Calendar;

/**
 * Виджет с расписанием.
 */
public class ScheduleWidget extends AppWidgetProvider {

    public static final String SCHEDULE_DAY_TIME = "widget_schedule_day_time";
    public static final String SCHEDULE_NAME = "widget_schedule_name";

    private static final String ACTION_SCHEDULE_DAY_CLICKED = "action_schedule_day_clicked";

    /**
     * Обновляет данные на виджете с расписанием.
     * @param context контекст.
     * @param appWidgetManager менеджер виджетов.
     * @param appWidgetId ID виджета.
     */
    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        ScheduleWidgetConfigureActivity.WidgetData widgetData =
                ScheduleWidgetConfigureActivity.loadPref(context, appWidgetId);

        String scheduleName = widgetData.scheduleName();
        if (scheduleName == null) {
            scheduleName = context.getString(R.string.widget_schedule_name);
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_schedule);

        // для открытия приложения на распиании
        Bundle scheduleBundle = ScheduleViewFragment.createBundle(scheduleName,
                SchedulePreference.createPath(context, scheduleName));

        PendingIntent schedulePendingIntent = new NavDeepLinkBuilder(context)
                .setComponentName(MainActivity.class)
                .setGraph(R.navigation.activity_main_nav_graph)
                .setDestination(R.id.nav_schedule_view_fragment)
                .setArguments(scheduleBundle)
                .createPendingIntent();

        views.setTextViewText(R.id.widget_schedule_name, scheduleName);
        views.setOnClickPendingIntent(R.id.widget_schedule_name, schedulePendingIntent);

        // для открытия приложения на расписании на определенном дне
        Intent scheduleDayIntent = new Intent(context, ScheduleWidget.class);
        scheduleDayIntent.setAction(ACTION_SCHEDULE_DAY_CLICKED);
        PendingIntent scheduleDayPendingIntent = PendingIntent
                .getBroadcast(context, appWidgetId, scheduleDayIntent, 0);

        views.setPendingIntentTemplate(R.id.widget_schedule_list, scheduleDayPendingIntent);

        // установка адаптера
        Intent dataIntent = new Intent(context, ScheduleWidgetRemoteFactory.Service.class);
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

            Calendar date = (Calendar) intent.getSerializableExtra(SCHEDULE_DAY_TIME);
            String scheduleName =  intent.getStringExtra(SCHEDULE_NAME);

            if (scheduleName == null) {
                return;
            }

            // создание intent'а на открытие расписание на определенном дне
            Bundle scheduleDayBundle = ScheduleViewFragment.createBundle(scheduleName,
                    SchedulePreference.createPath(context, scheduleName), date);

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
            ScheduleWidgetConfigureActivity.deletePref(context, appWidgetId);
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
