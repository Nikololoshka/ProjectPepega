package com.vereshchagin.nikolay.stankinschedule.utils.extensions

import java.util.*

/**
 *
 */
fun String.toTitleString(locale: Locale = Locale.getDefault()): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
}