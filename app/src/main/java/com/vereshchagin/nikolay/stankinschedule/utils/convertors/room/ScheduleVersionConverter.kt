package com.vereshchagin.nikolay.stankinschedule.utils.convertors.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.ScheduleVersion

class ScheduleVersionConverter {

    @TypeConverter
    fun fromVersions(versions: List<ScheduleVersion>): String {
        return Gson().toJson(versions)
    }

    @TypeConverter
    fun toVersions(json: String): List<ScheduleVersion> {
        val type = object : TypeToken<List<ScheduleVersion>>() {}.type
        return Gson().fromJson(json, type)
    }
}