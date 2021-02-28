package com.vereshchagin.nikolay.stankinschedule.utils.convertors.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Date

class PairDateConvertor {

    @TypeConverter
    fun toDate(date: String): Date {
        return Date(Gson().fromJson(date, JsonElement::class.java))
    }

    @TypeConverter
    fun fromDate(date: Date): String {
        return date.toJson().toString()
    }
}