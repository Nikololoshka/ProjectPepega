package com.vereshchagin.nikolay.stankinschedule.utils.convertors.room

import androidx.room.TypeConverter
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Time


class PairTimeConvertor {
    @TypeConverter
    fun toTime(time: String): Time {
        val (start, end) = time.split('-')
        return Time(start, end)
    }

    @TypeConverter
    fun fromTime(time: Time): String {
        return time.toString()
    }
}