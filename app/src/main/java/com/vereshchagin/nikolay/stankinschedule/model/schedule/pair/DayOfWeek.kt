package com.vereshchagin.nikolay.stankinschedule.model.schedule.pair

import android.os.Parcelable
import com.vereshchagin.nikolay.stankinschedule.model.schedule.DateDayOfWeekException
import kotlinx.parcelize.Parcelize
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
         * Возвращает день недели соответствующей даты.
         * @param date дата.
         * @return день недели.
         * @throws DateDayOfWeekException если не удалось узнать день недели.
         */
        fun of(date: LocalDate) : DayOfWeek {
            return when(date.dayOfWeek) {
                DateTimeConstants.MONDAY -> MONDAY
                DateTimeConstants.TUESDAY -> TUESDAY
                DateTimeConstants.WEDNESDAY -> WEDNESDAY
                DateTimeConstants.THURSDAY -> THURSDAY
                DateTimeConstants.FRIDAY -> FRIDAY
                DateTimeConstants.SATURDAY -> SATURDAY
                else -> {
                    throw DateDayOfWeekException( "Invalid day of week: $date")
                }
            }
        }
    }
}