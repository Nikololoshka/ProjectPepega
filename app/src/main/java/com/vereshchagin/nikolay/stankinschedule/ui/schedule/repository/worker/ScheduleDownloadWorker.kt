package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.worker

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.*
import com.vereshchagin.nikolay.stankinschedule.MainActivity
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRemoteRepository
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.ScheduleViewFragment
import com.vereshchagin.nikolay.stankinschedule.utils.NotificationUtils
import org.joda.time.DateTimeUtils

/**
 * Worker для скачивание расписания с репозитория
 */
class ScheduleDownloadWorker(
    context: Context, workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val scheduleName = inputData.getString(SCHEDULE_NAME)!!
        val versionName = inputData.getString(SCHEDULE_VERSION_NAME)!!
        val repositoryPath = inputData.getString(REPOSITORY_PATH)!!

        val scheduleId = inputData.getInt(SCHEDULE_ID, 0)
        val versionId = inputData.getInt(SCHEDULE_VERSION_ID, 0)
        val notificationId = NotificationUtils.MODULE_JOURNAL_IDS + scheduleId * 100 + versionId

        // подготовка уведомлений
        val manager = NotificationManagerCompat.from(applicationContext)
        val notificationBuilder = NotificationUtils.createCommonNotification(applicationContext)
            .setContentTitle("$scheduleName ($versionName)")
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
            ScheduleRemoteRepository(applicationContext)
                .downloadSchedule(scheduleName, repositoryPath)

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
            Log.e(TAG, "doWork: ", e)

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

        private const val TAG = "SchedulWorkerLog"

        private const val REPOSITORY_CATEGORY = "repository_category"
        private const val NOTIFICATION_ID = "notification_id"

        private const val REPOSITORY_PATH = "repository_path"
        private const val SCHEDULE_NAME = "schedule_name"
        private const val SCHEDULE_ID = "schedule_id"
        private const val SCHEDULE_VERSION_NAME = "schedule_version_name"
        private const val SCHEDULE_VERSION_ID = "schedule_version_id"

        /**
         * Запускает worker для скачивания расписания.
         */
        @JvmStatic
        fun startWorker(
            context: Context,
            scheduleName: String,
            scheduleId: Int,
            versionName: String,
            scheduleVersionId: Int,
            vararg paths: String,
        ) {
            val manager = WorkManager.getInstance(context)
            val path = paths.joinToString("/")
            val workerName = "Worker-$scheduleId-$scheduleVersionId"

            val worker = OneTimeWorkRequest.Builder(ScheduleDownloadWorker::class.java)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setInputData(
                    Data.Builder()
                        .putString(REPOSITORY_PATH, path)
                        .putString(SCHEDULE_NAME, scheduleName)
                        .putString(SCHEDULE_VERSION_NAME, versionName)
                        .putInt(SCHEDULE_ID, scheduleId)
                        .putInt(SCHEDULE_VERSION_ID, scheduleVersionId)
                        .build()
                )
                // .addTag(workerName)
                .build()

            manager.enqueueUniqueWork(workerName, ExistingWorkPolicy.KEEP, worker)
        }

        /**
         * Запускает worker для скачивания расписания.
         * @param category категория расписания.
         * @param scheduleName название расписания.
         */
        @JvmStatic
        fun startWorker(context: Context, category: String, scheduleName: String, id: Int) {
            val manager = WorkManager.getInstance(context)
            val workerName = "$category-$scheduleName"

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