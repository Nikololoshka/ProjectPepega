package com.vereshchagin.nikolay.stankinschedule.schedule.core.data.db

import androidx.room.TypeConverter
import org.joda.time.DateTime

/**
 * Конвертер даты для Room DB.
 */
class DateTimeConverter {

    @TypeConverter
    fun fromDateTime(dateTime: DateTime?): String? {
        return dateTime?.toString()
    }

    @TypeConverter
    fun toDateTime(dateTime: String?): DateTime? {
        return dateTime?.let { DateTime.parse(it) }
    }
}