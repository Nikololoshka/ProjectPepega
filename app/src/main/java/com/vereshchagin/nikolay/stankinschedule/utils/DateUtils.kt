package com.vereshchagin.nikolay.stankinschedule.utils

import android.os.Build
import java.text.SimpleDateFormat
import java.time.temporal.ChronoUnit
import java.util.*


fun parseDate(inputDate: String, pattern: String = "yyyy-MM-dd HH:mm:ssX"): Calendar {
    val date = Calendar.getInstance()
    date.time = SimpleDateFormat(pattern, Locale.ROOT).parse(inputDate)!!
    return date
}

fun formatDate(inputDate: Calendar, pattern: String = "dd.MM.yyyy") : String {
    return SimpleDateFormat(pattern, Locale.ROOT).format(inputDate.time)
}

class DateUtils {

    companion object {
        const val FULL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ssX"


        fun minutesBetween(first: Calendar, second: Calendar): Long {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return ChronoUnit.MINUTES.between(first.toInstant(), second.toInstant())
            }

            val diff = first.timeInMillis - second.timeInMillis
            return diff / (1000 * 60)
        }
    }
}