package com.vereshchagin.nikolay.stankinschedule.model.schedule.pair

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate

/**
 * Дни недели даты в расписании.
 */
@Parcelize
enum class DayOfWeek : Parcelable {
    /**
     * Понедельник.
     */
    MONDAY,

    /**
     * Вторник.
     */
    TUESDAY,

    /**
     * Среда.
     */
    WEDNESDAY,

    /**
     * Четверг.
     */
    THURSDAY,

    /**
     * Пятница.
     */
    FRIDAY,

    /**
     * Суббота.
     */
    SATURDAY;

    companion object {
        /**
         * Возвращает день недели соотвествующей даты.
         * @param date дата.
         * @return день недели.
         * @throws IllegalArgumentException если не удалось узнать день недели.
         */
        fun of(date: DateTime) : DayOfWeek {
            return when(date.dayOfWeek) {
                DateTimeConstants.MONDAY -> MONDAY
                DateTimeConstants.TUESDAY -> TUESDAY
                DateTimeConstants.WEDNESDAY -> WEDNESDAY
                DateTimeConstants.THURSDAY -> THURSDAY
                DateTimeConstants.FRIDAY -> FRIDAY
                DateTimeConstants.SATURDAY -> SATURDAY
                else -> {
                    throw IllegalArgumentException( "Invalid day of week: $date")
                }
            }
        }

        fun of(date: LocalDate): DayOfWeek {
            return of(date.toDateTimeAtStartOfDay())
        }
    }
}