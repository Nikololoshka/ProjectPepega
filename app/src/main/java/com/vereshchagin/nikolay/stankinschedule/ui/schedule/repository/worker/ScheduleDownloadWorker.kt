package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.worker

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.*
import com.vereshchagin.nikolay.stankinschedule.MainActivity
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRemoteRepository
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
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
        val saveScheduleName = inputData.getString(SCHEDULE_SAVE_NAME)!!
        val replaceExist = inputData.getBoolean(SCHEDULE_REPLACE_EXIST, false)

        val scheduleName = inputData.getString(SCHEDULE_NAME)!!
        val versionName = inputData.getString(SCHEDULE_VERSION_NAME)!!
        val repositoryPath = inputData.getString(REPOSITORY_PATH)!!

        val scheduleId = inputData.getInt(SCHEDULE_ID, 0)
        val versionId = inputData.getInt(SCHEDULE_VERSION_ID, 0)
        val notificationId = NotificationUtils.MODULE_JOURNAL_IDS + scheduleId * 100 + versionId

        val isSync = inputData.getBoolean(SCHEDULE_SYNC, false)

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
            val remoteRepository = ScheduleRemoteRepository(applicationContext)
            val response = remoteRepository.downloadSchedule(repositoryPath)

            val scheduleRepository = ScheduleRepository(applicationContext)
            scheduleRepository.saveResponse(saveScheduleName, response, replaceExist, isSync)

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
            e.printStackTrace()

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
                .setContentText(
                    getString(
                        if (isSync)
                            R.string.repository_schedule_synced
                        else
                            R.string.repository_schedule_loaded
                    )
                )
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

        private const val TAG = "ScheduleWorkerLog"

        private const val REPOSITORY_PATH = "repository_path"
        private const val SCHEDULE_REPLACE_EXIST = "schedule_replace_exist"
        private const val SCHEDULE_SAVE_NAME = "schedule_save_name"
        private const val SCHEDULE_NAME = "schedule_name"
        private const val SCHEDULE_ID = "schedule_id"
        private const val SCHEDULE_VERSION_NAME = "schedule_version_name"
        private const val SCHEDULE_VERSION_ID = "schedule_version_id"
        private const val SCHEDULE_SYNC = "schedule_sync"

        /**
         * Запускает worker для скачивания расписания.
         * @param context контекст приложения.
         * @param saveScheduleName имя, под которым будет сохранено расписание.
         * @param replaceExist заменять ли существующие расписание.
         * @param scheduleName названия расписания.
         * @param scheduleId уникальный ID расписания.
         * @param versionName название версии расписания.
         * @param scheduleVersionId уникальный ID версии.
         * @param isSync будет ли расписание синхронизироваться.
         * @param paths путь к расписанию в удаленном репозитории.
         */
        @JvmStatic
        fun startWorker(
            context: Context,
            saveScheduleName: String,
            replaceExist: Boolean,
            scheduleName: String,
            scheduleId: Int,
            versionName: String,
            scheduleVersionId: Int,
            isSync: Boolean,
            vararg paths: String,
        ) {
            val manager = WorkManager.getInstance(context)
            val path = paths.joinToString("/")

            // уникальное имя worker'а
            val workerName = "Worker-$scheduleId-$scheduleVersionId"

            val worker = OneTimeWorkRequest.Builder(ScheduleDownloadWorker::class.java)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setInputData(
                    Data.Builder()
                        .putBoolean(SCHEDULE_REPLACE_EXIST, replaceExist)
                        .putString(REPOSITORY_PATH, path)
                        .putString(SCHEDULE_SAVE_NAME, saveScheduleName)
                        .putString(SCHEDULE_NAME, scheduleName)
                        .putString(SCHEDULE_VERSION_NAME, versionName)
                        .putInt(SCHEDULE_ID, scheduleId)
                        .putInt(SCHEDULE_VERSION_ID, scheduleVersionId)
                        .putBoolean(SCHEDULE_SYNC, isSync)
                        .build()
                )
                // .addTag(workerName)
                .build()

            manager.enqueueUniqueWork(workerName, ExistingWorkPolicy.KEEP, worker)
        }
    }
}