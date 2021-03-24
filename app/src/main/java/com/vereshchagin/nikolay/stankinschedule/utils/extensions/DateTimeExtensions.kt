package com.vereshchagin.nikolay.stankinschedule.utils.extensions

import org.joda.time.LocalDate

/**
 * Возвращает отформатированную дату из строки типа "yyyy-MM-dd".
 */
fun String.toPrettyDate(): String =
    LocalDate.parse(this).toString("dd.MM.yyyy")
