package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vereshchagin.nikolay.stankinschedule.db.MainApplicationDatabase
import kotlinx.coroutines.flow.first

class ScheduleSyncWorker(
    context: Context, workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {

        val db = MainApplicationDatabase.database(applicationContext)
        val syncs = db.schedules().getScheduleSyncList().first()

        return Result.retry()
    }
}