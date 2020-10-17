package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.worker

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleServerRepository
import com.vereshchagin.nikolay.stankinschedule.utils.NotificationUtils

/**
 * Worker для скачивание расписания с репозитория
 */
class ScheduleDownloadWorker(
    context: Context, workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val category = inputData.getString(REPOSITORY_CATEGORY)!!
        val scheduleName = inputData.getString(SCHEDULE_NAME)!!

        val downloader = ScheduleServerRepository(applicationContext.cacheDir)
            .scheduleDownloader(category, scheduleName).downloadUrl.toString()


        val notification = NotificationUtils.createCommonNotification(applicationContext)
            .setContentTitle("$category/$scheduleName")
            .setContentText(downloader)
            .setSmallIcon(R.drawable.ic_notification_file_download)
            .build()

        val manager = NotificationManagerCompat.from(applicationContext)
        NotificationUtils.notifyCommon(applicationContext, manager, 100, notification)

        return Result.success()
    }

    companion object {

        private const val REPOSITORY_CATEGORY = "repository_category"
        private const val SCHEDULE_NAME = "schedule_name"

        /**
         * Запускает worker для скачаивания расписания.
         * @param category категория расписания.
         * @param scheduleName название расписания.
         */
        fun startWorker(context: Context, category: String, scheduleName: String) {
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
                        .build()
                )
                // .addTag(workerName)
                .build()

            manager.enqueueUniqueWork(workerName, ExistingWorkPolicy.KEEP, worker)
        }
    }
}