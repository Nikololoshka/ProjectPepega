package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.worker

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.*
import com.vereshchagin.nikolay.stankinschedule.MainActivity
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRemoteRepository
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.ScheduleViewFragment
import com.vereshchagin.nikolay.stankinschedule.utils.NotificationUtils
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.FLAG_MUTABLE_COMPAT
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.joda.time.DateTimeUtils

/**
 * Worker для скачивание расписания с репозитория
 */
@HiltWorker
class ScheduleDownloadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val remoteRepository: ScheduleRemoteRepository,
    private val scheduleRepository: ScheduleRepository,
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val saveScheduleName = inputData.getString(SCHEDULE_SAVE_NAME)!!
        val replaceExist = inputData.getBoolean(SCHEDULE_REPLACE_EXIST, false)

        val scheduleId = inputData.getInt(SCHEDULE_ID, 0)
        val scheduleUpdateId = inputData.getInt(SCHEDULE_UPDATE_ID, 0)
        val notificationId = NotificationUtils.COMMON_IDS + scheduleId * 1000 + scheduleUpdateId

        // подготовка уведомлений
        val manager = NotificationManagerCompat.from(applicationContext)
        val notificationBuilder = NotificationUtils.createCommonNotification(applicationContext)
            .setContentTitle(saveScheduleName)
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
            val scheduleResponse = remoteRepository.downloadSchedule(scheduleId, scheduleUpdateId)
            val savedScheduleId = scheduleRepository.saveResponse(
                saveScheduleName, scheduleResponse, replaceExist, false
            )

            val scheduleViewPendingIntent = NavDeepLinkBuilder(applicationContext)
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.activity_main_nav_graph)
                .setDestination(R.id.nav_schedule_view_fragment)
                .setArguments(ScheduleViewFragment.createBundle(savedScheduleId))
                .createTaskStackBuilder()
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or FLAG_MUTABLE_COMPAT)

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

        } catch (e: Exception) {
            // ошибка загрузки
            NotificationUtils.notifyCommon(
                applicationContext,
                manager,
                notificationId,
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

        const val WORKER_TAG = "schedule_download_worker_tag"

        private const val SCHEDULE_REPLACE_EXIST = "schedule_replace_exist"
        private const val SCHEDULE_SAVE_NAME = "schedule_save_name"
        private const val SCHEDULE_ID = "schedule_id"
        private const val SCHEDULE_UPDATE_ID = "schedule_update_id"

        /**
         * Запускает worker для скачивания расписания.
         * @param context контекст приложения.
         * @param saveScheduleName имя, под которым будет сохранено расписание.
         * @param replaceExist заменять ли существующие расписание.
         * @param scheduleId уникальный ID расписания.
         * @param scheduleUpdateId уникальный ID версии.
         */
        fun startWorker(
            context: Context,
            saveScheduleName: String,
            replaceExist: Boolean,
            scheduleId: Int,
            scheduleUpdateId: Int,
        ) {
            val manager = WorkManager.getInstance(context)

            // уникальное имя worker'а
            val workerName = "Worker-$scheduleId-$scheduleUpdateId"

            val worker = OneTimeWorkRequest.Builder(ScheduleDownloadWorker::class.java)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setInputData(
                    Data.Builder()
                        .putString(SCHEDULE_SAVE_NAME, saveScheduleName)
                        .putBoolean(SCHEDULE_REPLACE_EXIST, replaceExist)
                        .putInt(SCHEDULE_ID, scheduleId)
                        .putInt(SCHEDULE_UPDATE_ID, scheduleUpdateId)
                        .build()
                )
                .addTag(WORKER_TAG)
                .build()

            manager.enqueueUniqueWork(workerName, ExistingWorkPolicy.KEEP, worker)
        }
    }
}