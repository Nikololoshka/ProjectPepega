package com.vereshchagin.nikolay.stankinschedule.core.ui.notification

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/**
 * Диспетчер уведомлений.
 */
object NotificationUtils {

    const val CHANNEL_COMMON = "channel_common"
    const val CHANNEL_MODULE_JOURNAL = "channel_module_journal"

    const val MODULE_JOURNAL_IDS: Int = 1_000_000
    const val COMMON_IDS: Int = 2_000_000

    fun isNotificationAllow(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    @kotlin.jvm.Throws(SecurityException::class)
    fun notify(
        context: Context,
        manager: NotificationManagerCompat,
        id: Int,
        notification: Notification
    ) {
        manager.areNotificationsEnabled()
        if (isNotificationAllow(context)) {
            manager.notify(id, notification)
        }
    }

    @JvmStatic
    fun createCommonNotification(context: Context): NotificationCompat.Builder {
        return NotificationCompat
            .Builder(context, CHANNEL_COMMON)
            .setDefaults(Notification.DEFAULT_ALL)
    }

    @JvmStatic
    fun createModuleJournalNotification(context: Context): NotificationCompat.Builder {
        return NotificationCompat
            .Builder(context, CHANNEL_MODULE_JOURNAL)
            .setDefaults(Notification.DEFAULT_ALL)
    }
}