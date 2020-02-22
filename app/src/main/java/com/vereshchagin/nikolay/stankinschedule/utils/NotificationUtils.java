package com.vereshchagin.nikolay.stankinschedule.utils;

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
public class NotificationUtils {

    public static final String CHANNEL_COMMON = "channel_common";
    public static final String CHANNEL_MODULE_JOURNAL = "channel_module_journal";

    private static final String COMMON_PREFERENCE_TURN = "common_notification_turn";
    private static final String MODULE_JOURNAL_PREFERENCE_TURN = "mj_notification_turn";

    /**
     * Создает уведомление общего назначения.
     * @param context контекст приложения.
     * @return builder уведомления с настройками.
     */
    @NonNull
    public static NotificationCompat.Builder createCommonNotification(@NonNull Context context) {
        NotificationCompat.Builder notificationBuilder;
        notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_COMMON);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        return notificationBuilder;
    }

    /**
     * Отправляет уведомление общего назначения в соответствии с настройками.
     * @param context контекст приложения.
     * @param manager менеджер уведомлений.
     * @param id ID уведомления.
     * @param notification уведомление.
     */
    public static void notifyCommon(@NonNull Context context, NotificationManager manager,
                                    int id, @NonNull Notification notification) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (!preferences.getBoolean(COMMON_PREFERENCE_TURN, true)) {
                return;
            }
        }
        manager.notify(id, notification);
    }

    /**
     * Создает уведомление модульного журнала.
     * @param context контекст приложения.
     * @return builder уведомления с настройками.
     */
    @NonNull
    public static NotificationCompat.Builder createModuleJournalNotification(@NonNull Context context) {
        NotificationCompat.Builder notificationBuilder;
        notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_MODULE_JOURNAL);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        return notificationBuilder;
    }

    /**
     * Отправляет уведомление модульного журнала в соответствии с настройками.
     * @param context контекст приложения.
     * @param manager менеджер уведомлений.
     * @param id ID уведомления.
     * @param notification уведомление.
     */
    public static void notifyModuleJournal(@NonNull Context context, NotificationManager manager,
                                           int id, @NonNull Notification notification) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (!preferences.getBoolean(MODULE_JOURNAL_PREFERENCE_TURN, true)) {
                return;
            }
        }
        manager.notify(id, notification);
    }
}
