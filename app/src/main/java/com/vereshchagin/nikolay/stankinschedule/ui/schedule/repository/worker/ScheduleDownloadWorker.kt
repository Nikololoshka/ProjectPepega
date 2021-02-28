package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.worker

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.*
import com.vereshchagin.nikolay.stankinschedule.MainActivity
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepositoryKt
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

        val serverRepository = ScheduleServerRepository(applicationContext.cacheDir)
        val uri = serverRepository.scheduleUri(category, scheduleName)

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
            val response = serverRepository.downloader().schedule(uri.toString()).await()
            ScheduleRepositoryKt.saveResponse(applicationContext, scheduleName, response)

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
            .setArguments(ScheduleViewFragment.createBundle(scheduleName))
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

        private const val REPOSITORY_CATEGORY = "repository_category"
        private const val SCHEDULE_NAME = "schedule_name"
        private const val NOTIFICATION_ID = "notification_id"

        /**
         * Запускает worker для скачивания расписания.
         * @param category категория расписания.
         * @param scheduleName название расписания.
         */
        @JvmStatic
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