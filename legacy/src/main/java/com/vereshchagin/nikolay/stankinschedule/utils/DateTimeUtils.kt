package com.vereshchagin.nikolay.stankinschedule.utils

import org.joda.time.DateTime
import org.joda.time.Minutes

object DateTimeUtils {

    const val DEFAULT_DATE_PATTERN = "yyyy-MM-dd"
    const val PRETTY_DATE_PATTERN = "dd.MM.yyyy"

    fun between(yesterday: DateTime, today: DateTime): Int {
        return Minutes.minutesBetween(yesterday, today).minutes
    }


}