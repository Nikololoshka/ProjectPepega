package com.vereshchagin.nikolay.stankinschedule.utils

import android.app.Notification
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager

/**
 * Диспетчер уведомлений.
 */
object NotificationUtils {

    const val CHANNEL_COMMON = "channel_common"
    const val CHANNEL_MODULE_JOURNAL = "channel_module_journal"

    const val MODULE_JOURNAL_IDS = 100_000
    const val COMMON_IDS = 200_000

    private const val COMMON_PREFERENCE_TURN = "common_notification_turn"
    private const val MODULE_JOURNAL_PREFERENCE_TURN = "mj_notification_turn"

    /**
     * Создает уведомление общего назначения.
     * @param context контекст приложения.
     * @return builder уведомления с настройками.
     */
    @JvmStatic
    fun createCommonNotification(context: Context): NotificationCompat.Builder {
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, CHANNEL_COMMON)
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL)
        return notificationBuilder
    }

    /**
     * Отправляет уведомление общего назначения в соответствии с настройками.
     * @param context контекст приложения.
     * @param manager менеджер уведомлений.
     * @param id ID уведомления.
     * @param notification уведомление.
     */
    @JvmStatic
    fun notifyCommon(
        context: Context, manager: NotificationManagerCompat,
        id: Int, notification: Notification,
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            if (!preferences.getBoolean(COMMON_PREFERENCE_TURN, true)) {
                return
            }
        }
        manager.notify(id, notification)
    }

    /**
     * Создает уведомление модульного журнала.
     * @param context контекст приложения.
     * @return builder уведомления с настройками.
     */
    @JvmStatic
    fun createModuleJournalNotification(context: Context): NotificationCompat.Builder {
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, CHANNEL_MODULE_JOURNAL)
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL)
        return notificationBuilder
    }

    /**
     * Отправляет уведомление модульного журнала в соответствии с настройками.
     * @param context контекст приложения.
     * @param manager менеджер уведомлений.
     * @param id ID уведомления.
     * @param notification уведомление.
     */
    @JvmStatic
    fun notifyModuleJournal(
        context: Context,
        manager: NotificationManagerCompat,
        id: Int,
        notification: Notification,
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            if (!preferences.getBoolean(MODULE_JOURNAL_PREFERENCE_TURN, true)) {
                return
            }
        }
        manager.notify(id, notification)
    }
}