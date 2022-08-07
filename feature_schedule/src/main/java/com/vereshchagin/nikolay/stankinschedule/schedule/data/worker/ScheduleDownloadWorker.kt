package com.vereshchagin.nikolay.stankinschedule.schedule.data.worker

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.room.RoomDatabase
import androidx.work.*
import com.vereshchagin.nikolay.stankinschedule.core.utils.NotificationUtils
import com.vereshchagin.nikolay.stankinschedule.schedule.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.schedule.R
import com.vereshchagin.nikolay.stankinschedule.schedule.data.api.ScheduleRepositoryAPI
import com.vereshchagin.nikolay.stankinschedule.schedule.data.db.ScheduleDao
import com.vereshchagin.nikolay.stankinschedule.schedule.data.mapper.toScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.data.repository.FirebaseRemoteService
import com.vereshchagin.nikolay.stankinschedule.schedule.data.repository.ScheduleStorageImpl
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.RepositoryItem
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.repository.ScheduleRemoteService
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.repository.ScheduleStorage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.joda.time.DateTimeUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Worker для скачивание расписания с репозитория
 */
@HiltWorker
class ScheduleDownloadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val api: ScheduleRepositoryAPI,
    private val db: RoomDatabase,
    private val dao: ScheduleDao,
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        // TODO("Provides только Singleton элементы")
        val service: ScheduleRemoteService = FirebaseRemoteService(api)
        val storage: ScheduleStorage = ScheduleStorageImpl(db, dao)

        val scheduleName = inputData.getString(SCHEDULE_NAME)!!
        val schedulePath = inputData.getString(SCHEDULE_PATH)!!
        val scheduleCategory = inputData.getString(SCHEDULE_CATEGORY)!!
        val replaceExist = inputData.getBoolean(SCHEDULE_REPLACE_EXIST, false)

        val notificationId = createID()

        // подготовка уведомлений
        val manager = NotificationManagerCompat.from(applicationContext)

        val notificationBuilder = NotificationUtils.createCommonNotification(applicationContext)
            .setContentTitle(scheduleName)
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
            // загрузка расписания
            val pairs = service.schedule(scheduleCategory, schedulePath)
            val model = pairs.toScheduleModel(scheduleName)
            storage.saveSchedule(model, replaceExist)

            // уведомление об окончании загрузки
            NotificationUtils.notifyCommon(
                applicationContext, manager, notificationId,
                notificationBuilder
                    .setWhen(DateTimeUtils.currentTimeMillis())
                    .setProgress(0, 0, false)
                    .setContentText(getString(R.string.repository_schedule_loaded))
                    .setAutoCancel(true)
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

            if (BuildConfig.DEBUG) {
                Log.e(TAG, "doWork: ", e)
            }

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

    private fun createID(): Int {
        return SimpleDateFormat("ddHHmmss", Locale.US).format(Date()).toInt()
    }

    companion object {

        private const val TAG = "ScheduleWorkerLog"

        private const val WORKER_TAG = "schedule_download_worker_tag"

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
                .addTag(WORKER_TAG)
                .build()

            manager.enqueueUniqueWork(workerName, ExistingWorkPolicy.KEEP, worker)
        }
    }
}