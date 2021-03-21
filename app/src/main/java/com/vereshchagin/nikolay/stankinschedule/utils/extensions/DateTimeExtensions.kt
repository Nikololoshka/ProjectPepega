package com.vereshchagin.nikolay.stankinschedule.utils.extensions

import org.joda.time.LocalDate

/**
 *
 */
fun String.toPrettyDate(): String = LocalDate.parse(this).toString("dd.MM.yyyy")
