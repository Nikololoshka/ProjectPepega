package com.github.nikololoshka.pepegaschedule.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Сервис, который создает адаптер по оновлению данных в виджете.
 */
public class ScheduleAppWidgetRemoteService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ScheduleAppWidgetRemoteFactory(getApplicationContext(), intent);
    }
}
