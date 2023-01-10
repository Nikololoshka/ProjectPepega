package com.vereshchagin.nikolay.stankinschedule.migration

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.api.PairJson
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.mapper.toPairModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.repository.SchedulePreference
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class Migrator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scheduleStorage: ScheduleStorage,
    private val schedulePreference: SchedulePreference
) {

    suspend fun migrate_2_0_0() {
        // Расписания
        val favorite = SchedulePreference_1_0.favorite(context)
        val scheduleNames = SchedulePreference_1_0.schedules(context)
        val gson = GsonBuilder().create()

        for (scheduleName in scheduleNames) {
            val path = SchedulePreference_1_0.createPath(context, scheduleName)

            try {
                val pairs = File(path).bufferedReader().use { reader ->
                    val type = object : TypeToken<List<PairJson>>() {}.type
                    gson.fromJson<List<PairJson>>(reader, type)!!
                }.map { it.toPairModel() }

                val model = ScheduleModel(ScheduleInfo(scheduleName)).apply {
                    pairs.forEach { pair -> add(pair) }
                }

                val scheduleId = scheduleStorage.saveSchedule(model)
                if (scheduleName == favorite) {
                    schedulePreference.setFavorite(scheduleId)
                }

            } catch (ignored: Exception) {

            }
        }
    }
}