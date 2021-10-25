package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.*
import com.vereshchagin.nikolay.stankinschedule.MainActivity
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.MarkType
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.StudentData
import com.vereshchagin.nikolay.stankinschedule.repository.ModuleJournalRepository
import com.vereshchagin.nikolay.stankinschedule.utils.NotificationUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * Worker для просмотра обновлений в модульном журнале.
 */
@HiltWorker
class ModuleJournalWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: ModuleJournalRepository,
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val manager = NotificationManagerCompat.from(applicationContext)

        try {
            val semester = checkStudentData(repository, manager, NOTIFICATION_ID)
            checkSemesterMarks(semester, repository, manager, NOTIFICATION_ID + 1)

        } catch (e: Exception) {
            return Result.failure()
        }

        return Result.success()
    }

    /**
     * Проверяет изменения в данных студента (добавление нового семестра).
     */
    private suspend fun checkStudentData(
        repository: ModuleJournalRepository,
        manager: NotificationManagerCompat,
        notificationId: Int
    ): String {
        val cacheStudentData = repository.loadCacheStudentData()
        if (cacheStudentData == null || !cacheStudentData.isValid()) {
            val newStudentData = repository.loadStudentData(true)
            if (cacheStudentData != null) {
                val changes = compareStudentData(newStudentData, cacheStudentData)
                if (changes.isNotEmpty()) {
                    val mjPendingIntent = NavDeepLinkBuilder(applicationContext)
                        .setComponentName(MainActivity::class.java)
                        .setGraph(R.navigation.activity_main_nav_graph)
                        .setDestination(R.id.nav_module_journal_fragment)
                        .createPendingIntent()

                    val notification =
                        NotificationUtils.createModuleJournalNotification(applicationContext)
                            .setContentTitle(getString(R.string.notification_mj))
                            .setContentText(changes)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.ic_nav_module_journal)
                            .setAutoCancel(true)
                            .setContentIntent(mjPendingIntent)
                            .build()

                    NotificationUtils.notifyModuleJournal(
                        applicationContext, manager, notificationId, notification
                    )
                }
            }
            return newStudentData.semesters.last()
        }
        return cacheStudentData.semesters.last()
    }

    /**
     * Проверяет изменения оценок в семестре студента.
     */
    private suspend fun checkSemesterMarks(
        semester: String,
        repository: ModuleJournalRepository,
        manager: NotificationManagerCompat,
        notificationId: Int
    ) {
        val cacheSemesterMarks = repository.loadCacheSemesterMarks(semester)
        if (cacheSemesterMarks == null || !cacheSemesterMarks.isValid(true)) {
            val newSemesterMarks = repository.loadSemesterMarks(
                semester, refresh = true, last = true
            )
            if (cacheSemesterMarks != null) {
                var changes = compareSemesterMarks(newSemesterMarks, cacheSemesterMarks)
                if (changes.isNotEmpty()) {
                    changes = getString(R.string.notification_mj_new_marks) + "\n" + changes

                    val mjPendingIntent = NavDeepLinkBuilder(applicationContext)
                        .setComponentName(MainActivity::class.java)
                        .setGraph(R.navigation.activity_main_nav_graph)
                        .setDestination(R.id.nav_module_journal_fragment)
                        .createPendingIntent()

                    val notification =
                        NotificationUtils.createModuleJournalNotification(applicationContext)
                            .setContentTitle(getString(R.string.notification_mj))
                            .setContentText(changes)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.ic_nav_module_journal)
                            .setStyle(
                                NotificationCompat.BigTextStyle()
                                    .bigText(changes)
                                    .setBigContentTitle(getString(R.string.notification_mj))
                            )
                            .setAutoCancel(true)
                            .setContentIntent(mjPendingIntent)
                            .build()

                    NotificationUtils.notifyModuleJournal(
                        applicationContext, manager, notificationId, notification
                    )
                }
            }
        }
    }

    /**
     * Сравнивает текущие данные студента с предыдущими.
     */
    private fun compareStudentData(
        newStudentData: StudentData,
        oldStudentData: StudentData
    ): String {
        val changes = arrayListOf<String>()

        for (newSemester in newStudentData.semesters) {
            if (!oldStudentData.semesters.contains(newSemester)) {
                changes.add(getString(R.string.notification_mj_new_semester, newSemester))
            }
        }

        return changes.joinToString("\n")
    }

    /**
     * Сравнивает текущие оценки студента с полученными ранее.
     */
    private fun compareSemesterMarks(
        newSemesterMarks: SemesterMarks,
        oldSemesterMarks: SemesterMarks
    ): String {
        val changes = arrayListOf<String>()

        for (newDiscipline in newSemesterMarks.disciplines) {
            for (oldDiscipline in oldSemesterMarks.disciplines) {
                if (newDiscipline.title == oldDiscipline.title) {
                    for (type in MarkType.values()) {
                        val newMark = newDiscipline[type]
                        val oldMark = oldDiscipline[type]
                        if (newMark != null && oldMark != null) {
                            if (newMark != oldMark) {
                                changes.add("${newDiscipline.title}: $newMark (${type.tag})")
                            }
                        }
                    }
                    break
                }
            }
        }

        return changes.joinToString("\n")
    }

    /**
     * Возвращает строку из ресурсов.
     */
    private fun getString(@StringRes id: Int, arg: String): String {
        return applicationContext.getString(id, arg)
    }

    /**
     * Возвращает строку из ресурсов.
     */
    private fun getString(@StringRes id: Int): String {
        return applicationContext.getString(id)
    }

    companion object {

        private const val MODULE_JOURNAL_WORK_TAG = "ModuleJournalWorker"
        private const val NOTIFICATION_ID = 10

        /**
         * Запускает worker'а для просмотра обновлений в модульном журнале.
         */
        @JvmStatic
        fun startWorker(context: Context) {
            val manager = WorkManager.getInstance(context)

            val workerClass = ModuleJournalWorker::class.java
            val worker = PeriodicWorkRequest.Builder(workerClass, 1, TimeUnit.HOURS)
                .addTag(MODULE_JOURNAL_WORK_TAG)
                .setInitialDelay(1, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            manager.enqueueUniquePeriodicWork(
                MODULE_JOURNAL_WORK_TAG, ExistingPeriodicWorkPolicy.KEEP, worker
            )
        }

        /**
         * Отменяет все назначенные работы worker'а.
         */
        @JvmStatic
        fun cancelWorker(context: Context) {
            val manager = WorkManager.getInstance(context)
            manager.cancelAllWorkByTag(MODULE_JOURNAL_WORK_TAG)
        }
    }
}