package com.vereshchagin.nikolay.stankinschedule.utils.convertors.room

import androidx.room.TypeConverter
import org.joda.time.DateTime

/**
 * Конвертер даты для Room DB.
 */
class DateTimeConverter {

    @TypeConverter
    fun fromDateTime(dateTime: DateTime): String {
        return dateTime.toString()
    }

    @TypeConverter
    fun toDateTime(dateTime: String): DateTime {
        return DateTime.parse(dateTime)
    }
}