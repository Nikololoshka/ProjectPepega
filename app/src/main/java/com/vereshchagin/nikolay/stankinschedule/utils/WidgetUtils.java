package com.vereshchagin.nikolay.stankinschedule.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;

import androidx.annotation.NonNull;

import com.vereshchagin.nikolay.stankinschedule.widget.ScheduleWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * Вспомогательные функции по работе с виджетами.
 */
public class WidgetUtils {

    /**
     * Возвращает текущий список виджетов с расписаниями.
     * @param context контекст.
     * @return список виджетов.
     */
    @NonNull
    public static List<Integer> scheduleWidgets(@NonNull Context context) {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] ids = widgetManager.getAppWidgetIds(new ComponentName(context, ScheduleWidget.class));

        List<Integer> idsList = new ArrayList<>(ids.length);
        for (int id : ids) {
            idsList.add(id);
        }

        return idsList;
    }
}
