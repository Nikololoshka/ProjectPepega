package com.vereshchagin.nikolay.stankinschedule.core.ui

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.Minutes
import java.util.*

infix fun DateTime.subMinutes(other: DateTime): Int {
    return Minutes.minutesBetween(this, other).minutes
}

fun formatDate(date: String, pattern: String = "dd.MM.yyyy", locale: Locale = Locale.ROOT): String {
    return LocalDate.parse(date).toString(pattern, locale)
}