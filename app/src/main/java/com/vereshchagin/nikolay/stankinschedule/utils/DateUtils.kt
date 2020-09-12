package com.vereshchagin.nikolay.stankinschedule.utils

import android.os.Build
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.abs


class DateUtils {

    companion object {
        const val PRETTY_FORMAT = "dd.MM.yyyy"

        const val SHORT_DATE_FORMAT = "yyyy-MM-dd"
        const val FULL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ssX"
        const val FULL_DATE_FORMAT_API_23 = "yyyy-MM-dd HH:mm:ss"

        fun minutesBetween(first: Calendar, second: Calendar): Long {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return abs(ChronoUnit.MINUTES.between(first.toInstant(), second.toInstant()))
            }

            val diff = first.timeInMillis - second.timeInMillis
            return abs(diff / (1000 * 60))
        }

        fun parseDate(inputDate: String, pattern: String = SHORT_DATE_FORMAT): Calendar? {
            val date = Calendar.getInstance()
            try {
                date.time = SimpleDateFormat(pattern, Locale.ROOT).parse(inputDate)!!
                return date
            } catch (ignored: ParseException) {

            }
            return null
        }

        fun formatDate(inputDate: Calendar, pattern: String = "dd.MM.yyyy") : String {
            return SimpleDateFormat(pattern, Locale.ROOT).format(inputDate.time)
        }
    }
}