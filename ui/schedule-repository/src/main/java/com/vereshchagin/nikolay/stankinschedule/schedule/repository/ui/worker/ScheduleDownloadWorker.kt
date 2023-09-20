package com.vereshchagin.nikolay.stankinschedule.schedule.repository.ui.worker

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.annotation.StringRes
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.vereshchagin.nikolay.stankinschedule.core.ui.notification.NotificationUtils
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.model.RepositoryItem
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.usecase.RepositoryLoaderUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.ui.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.joda.time.DateTimeUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import com.vereshchagin.nikolay.stankinschedule.core.ui.R as R_core

/**
 * Worker для скачивание расписания с репозитория
 */
@HiltWorker
class ScheduleDownloadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val loaderUseCase: RepositoryLoaderUseCase
) : CoroutineWorker(context, workerParameters) {

    // private val manager = NotificationManagerCompat.from(context)

    override suspend fun doWork(): Result {

        val scheduleName = inputData.getString(SCHEDULE_NAME)!!
        val schedulePath = inputData.getString(SCHEDULE_PATH)!!
        val scheduleCategory = inputData.getString(SCHEDULE_CATEGORY)!!
        val replaceExist = inputData.getBoolean(SCHEDULE_REPLACE_EXIST, false)

        val notificationId = createID()

        val info = createForegroundInfo(scheduleName, notificationId)
        setForeground(info)

        download(scheduleCategory, schedulePath, scheduleName, replaceExist)

        return Result.success(
            Data.Builder()
                .putString("scheduleName", scheduleName)
                .build()
        )
    }

    private fun createForegroundInfo(scheduleName: String, notificationId: Int): ForegroundInfo {
        val cancel = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        val notification = NotificationUtils.createCommonNotification(applicationContext)
            .setContentTitle(scheduleName)
            .setTicker(scheduleName)
            .setSmallIcon(R.drawable.ic_notification_file_download)
            .setWhen(DateTimeUtils.currentTimeMillis())
            .setProgress(100, 0, true)
            .addAction(R.drawable.ic_notification_cancel, getString(R_core.string.cancel), cancel)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                notificationId,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(notificationId, notification)
        }
    }

    private suspend fun download(
        scheduleCategory: String,
        schedulePath: String,
        scheduleName: String,
        replaceExist: Boolean
    ) {
        setProgress(
            data = Data.Builder()
                .putString("scheduleName", scheduleName)
                .build()
        )
        loaderUseCase.loadSchedule(scheduleCategory, schedulePath, scheduleName, replaceExist)
    }

    /**
     * Получение строки из ресурсов.
     */
    private fun getString(@StringRes id: Int): String {
        return applicationContext.getString(id)
    }

    private fun createID(): Int {
        return SimpleDateFormat("ddHHmmss", Locale.US).format(Date()).toInt()
    }

    companion object {

        private const val TAG = "ScheduleWorkerLog"

        const val WORKER_TAG = "schedule_download_worker_tag"

        private const val SCHEDULE_REPLACE_EXIST = "schedule_replace_exist"
        private const val SCHEDULE_NAME = "schedule_save_name"
        private const val SCHEDULE_PATH = "schedule_path"
        private const val SCHEDULE_CATEGORY = "schedule_category"

        /**
         * Запускает worker для загрузки расписания.
         */
        fun startWorker(
            context: Context,
            scheduleName: String,
            item: RepositoryItem,
            replaceExist: Boolean,
        ) {
            val manager = WorkManager.getInstance(context)

            // уникальное имя worker'а
            val workerName = "ScheduleWorker-${item.name}-${item.category}"

            val worker = OneTimeWorkRequest.Builder(ScheduleDownloadWorker::class.java)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setInputData(
                    Data.Builder()
                        .putString(SCHEDULE_NAME, scheduleName)
                        .putString(SCHEDULE_PATH, item.path)
                        .putString(SCHEDULE_CATEGORY, item.category)
                        .putBoolean(SCHEDULE_REPLACE_EXIST, replaceExist)
                        .build()
                )
                .keepResultsForAtLeast(24, TimeUnit.HOURS)
                .addTag(WORKER_TAG)
                .build()

            manager.enqueueUniqueWork(workerName, ExistingWorkPolicy.KEEP, worker)
        }
    }
}