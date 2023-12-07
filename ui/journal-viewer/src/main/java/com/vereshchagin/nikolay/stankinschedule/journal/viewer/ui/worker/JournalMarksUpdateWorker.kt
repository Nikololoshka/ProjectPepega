package com.vereshchagin.nikolay.stankinschedule.journal.viewer.ui.worker

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.vereshchagin.nikolay.stankinschedule.core.ui.notification.NotificationUtils
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.exceptions.StudentAuthorizedException
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.usecase.JournalUpdateUseCase
import com.vereshchagin.nikolay.stankinschedule.journal.viewer.ui.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class JournalMarksUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val useCase: JournalUpdateUseCase
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        try {
            val manager = NotificationManagerCompat.from(applicationContext)
            val (student, newSemesters) = useCase.updateSemesters()
            if (newSemesters.isNotEmpty()) {
                sendSemestersNotification(manager, newSemesters)
            }

            if (student != null) {
                val lastSemester = student.semesters.lastOrNull()
                if (lastSemester != null) {
                    val newMarks = useCase.updateSemesterMarks(lastSemester)
                    if (newMarks.isNotEmpty()) {
                        sendMarksNotification(manager, newMarks)
                    }
                }
            }

            return Result.success()

        } catch (e: StudentAuthorizedException) {
            return Result.failure()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    private fun getString(@StringRes id: Int, vararg args: Any): String {
        return applicationContext.getString(id, args)
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = NotificationUtils.createModuleJournalNotification(applicationContext)
            .setContentTitle(getString(R.string.journal_notification))
            .setContentText(getString(R.string.journal_notification_update))
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_journal_notification)
            .setAutoCancel(true)
            .build()

        val notificationId = NotificationUtils.MODULE_JOURNAL_IDS;

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

    private fun sendSemestersNotification(
        manager: NotificationManagerCompat,
        newSemesters: Set<String>
    ) {
        val content = getString(R.string.journal_new_semester) + newSemesters.joinToString(", ")
        val notification = NotificationUtils.createModuleJournalNotification(applicationContext)
            .setContentTitle(getString(R.string.journal_notification))
            .setContentText(content)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_journal_notification)
            .setAutoCancel(true)
            .build()

        NotificationUtils.notify(
            context = applicationContext,
            manager = manager,
            id = NotificationUtils.MODULE_JOURNAL_IDS + 100,
            notification = notification
        )
    }

    private fun sendMarksNotification(
        manager: NotificationManagerCompat,
        newMarks: Set<String>
    ) {
        val content = getString(R.string.journal_new_marks) + "\n" + newMarks.joinToString("\n")
        val notification = NotificationUtils.createModuleJournalNotification(applicationContext)
            .setContentTitle(getString(R.string.journal_notification))
            .setContentText(content)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_journal_notification)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content)
                    .setBigContentTitle(getString(R.string.journal_notification))
            )
            .setAutoCancel(true)
            .build()

        NotificationUtils.notify(
            context = applicationContext,
            manager = manager,
            id = NotificationUtils.MODULE_JOURNAL_IDS + 200,
            notification = notification
        )
    }

    companion object {

        private const val TAG = "JournalMarksUpdateWorker"

        fun startWorker(context: Context) {
            val manager = WorkManager.getInstance(context)

            val workerClass = JournalMarksUpdateWorker::class.java
            val worker = PeriodicWorkRequest.Builder(workerClass, 2, TimeUnit.HOURS)
                .addTag(TAG)
                .setInitialDelay(1, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            manager.enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP, worker)
        }

        fun cancelWorker(context: Context) {
            val manager = WorkManager.getInstance(context)
            manager.cancelAllWorkByTag(TAG)
        }
    }
}
