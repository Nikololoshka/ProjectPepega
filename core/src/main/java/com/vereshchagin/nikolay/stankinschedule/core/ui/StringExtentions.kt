package com.vereshchagin.nikolay.stankinschedule.core.ui

import java.util.*

fun String.toTitleCase(locale: Locale = Locale.ROOT): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
}