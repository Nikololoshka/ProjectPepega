package com.vereshchagin.nikolay.stankinschedule.utils

import java.text.SimpleDateFormat
import java.util.*


fun parseDate(inputDate: String, pattern: String = "yyyy-MM-dd HH:mm:ssX"): Calendar {
    val date = Calendar.getInstance()
    date.time = SimpleDateFormat(pattern, Locale.ROOT).parse(inputDate)!!
    return date
}

fun formatDate(inputDate: Calendar, pattern: String = "dd.MM.yyyy") : String {
    return SimpleDateFormat(pattern, Locale.ROOT).format(inputDate.time)
}