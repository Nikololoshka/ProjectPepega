package com.github.nikololoshka.pepegaschedule.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;


/**
 * Диспетчер уведомлений.
 */
public class NotificationDispatcher {

    public static final String CHANNEL_COMMON = "channel_common";

    private static final String COMMON_PREFERENCE_TURN = "common_notification_turn";

    /**
     * Создает уведомение общего назначения.
     * @param context контекст приложения.
     * @return builder уведомления с настройками.
     */
    public static NotificationCompat.Builder createCommonNotification(@NonNull Context context) {
        NotificationCompat.Builder notificationBuilder;
        notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_COMMON);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        return notificationBuilder;
    }

    /**
     * Отправляет уведомление с соотвествии с настройками.
     * @param context контекст приложения.
     * @param manager менеджер уведомлений.
     * @param id ID уведомления.
     * @param notification уведомление.
     */
    public static void notify(@NonNull Context context, NotificationManager manager, int id, Notification notification) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (!preferences.getBoolean(COMMON_PREFERENCE_TURN, true)) {
                return;
            }
        }
        manager.notify(id, notification);
    }
}
