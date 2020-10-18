package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.worker

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.*
import com.vereshchagin.nikolay.stankinschedule.MainActivity
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleServerRepository
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.ScheduleViewFragment
import com.vereshchagin.nikolay.stankinschedule.utils.NotificationUtils
import org.joda.time.DateTimeUtils
import retrofit2.await

/**
 * Worker для скачивание расписания с репозитория
 */
class ScheduleDownloadWorker(
    context: Context, workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val category = inputData.getString(REPOSITORY_CATEGORY)!!
        val scheduleName = inputData.getString(SCHEDULE_NAME)!!
        val notificationId = inputData.getInt(NOTIFICATION_ID, 1000)

        val repository = ScheduleServerRepository(applicationContext.cacheDir)
        val uri = repository.scheduleUri(category, scheduleName)

        // подготовка уведомлений
        val manager = NotificationManagerCompat.from(applicationContext)
        val notificationBuilder = NotificationUtils.createCommonNotification(applicationContext)
            .setContentTitle("$category/$scheduleName")
            .setSmallIcon(R.drawable.ic_notification_file_download)

        // уведомление о начале загрузки
        NotificationUtils.notifyCommon(
            applicationContext, manager, notificationId,
            notificationBuilder
                .setWhen(DateTimeUtils.currentTimeMillis())
                .setProgress(100, 0, true)
                .build()
        )

        try {
            val response = repository.downloader().schedule(uri.toString()).await()
            ScheduleRepository().saveNew(applicationContext, response, scheduleName)

            // уведомляем о загруженном расписании
            val notificationIntent = Intent(SCHEDULE_DOWNLOADED_EVENT)
            LocalBroadcastManager.getInstance(applicationContext)
                .sendBroadcast(notificationIntent)

        } catch (e: Exception) {
            // ошибка загрузки
            NotificationUtils.notifyCommon(
                applicationContext, manager, notificationId,
                notificationBuilder
                    .setWhen(DateTimeUtils.currentTimeMillis())
                    .setProgress(0, 0, false)
                    .setContentText(getString(R.string.repository_loading_failure))
                    .setAutoCancel(true)
                    .build()
            )

            return Result.failure()
        }

        val scheduleViewPendingIntent = NavDeepLinkBuilder(applicationContext)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.activity_main_nav_graph)
            .setDestination(R.id.nav_schedule_view_fragment)
            .setArguments(ScheduleViewFragment.createBundle(
                scheduleName, ScheduleRepository().path(applicationContext, scheduleName)
            ))
            .createPendingIntent()

        // уведомление о окончании загрузки
        NotificationUtils.notifyCommon(
            applicationContext, manager, notificationId,
            notificationBuilder
                .setWhen(DateTimeUtils.currentTimeMillis())
                .setProgress(0, 0, false)
                .setContentText(getString(R.string.repository_schedule_loaded))
                .setAutoCancel(true)
                .setContentIntent(scheduleViewPendingIntent)
                .build()
        )

        return Result.success()
    }

    /**
     * Получение строки из ресурсов.
     */
    private fun getString(@StringRes id: Int): String {
        return applicationContext.getString(id)
    }

    companion object {

        const val SCHEDULE_DOWNLOADED_EVENT = "schedule_downloaded_event"
        private const val REPOSITORY_CATEGORY = "repository_category"
        private const val SCHEDULE_NAME = "schedule_name"
        private const val NOTIFICATION_ID = "notification_id"

        /**
         * Запускает worker для скачаивания расписания.
         * @param category категория расписания.
         * @param scheduleName название расписания.
         */
        fun startWorker(context: Context, category: String, scheduleName: String, id: Int) {
            val manager = WorkManager.getInstance(context)
            val workerName="$category-$scheduleName"

            val worker = OneTimeWorkRequest.Builder(ScheduleDownloadWorker::class.java)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setInputData(
                    Data.Builder()
                        .putString(REPOSITORY_CATEGORY, category)
                        .putString(SCHEDULE_NAME, scheduleName)
                        .putInt(NOTIFICATION_ID, id)
                        .build()
                )
                // .addTag(workerName)
                .build()

            manager.enqueueUniqueWork(workerName, ExistingWorkPolicy.KEEP, worker)
        }
    }
}