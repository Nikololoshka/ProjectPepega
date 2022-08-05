package com.vereshchagin.nikolay.stankinschedule.schedule.data.db.convertors

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.DateModel
import com.vereshchagin.nikolay.stankinschedule.schedule.utils.ScheduleJsonUtils

class PairDateConvertor {

    @TypeConverter
    fun toDate(date: String): DateModel {
        val element = Gson().fromJson(date, JsonElement::class.java)
        return ScheduleJsonUtils.dateFromJson(element)
    }

    @TypeConverter
    fun fromDate(date: DateModel): String {
        return ScheduleJsonUtils.toJson(date).toString()
    }
}