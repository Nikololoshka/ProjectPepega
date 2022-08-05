package com.vereshchagin.nikolay.stankinschedule.schedule.data.db.convertors

import androidx.room.TypeConverter
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.Time


class PairTimeConvertor {

    @TypeConverter
    fun toTime(time: String): Time {
        return Time.fromString(time)
    }

    @TypeConverter
    fun fromTime(time: Time): String {
        return time.toString()
    }
}