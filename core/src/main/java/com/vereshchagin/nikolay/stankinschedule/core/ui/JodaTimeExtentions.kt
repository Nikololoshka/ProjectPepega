package com.vereshchagin.nikolay.stankinschedule.core.ui

import org.joda.time.DateTime
import org.joda.time.Minutes

infix fun DateTime.subMinutes(other: DateTime): Int {
    return Minutes.minutesBetween(this, other).minutes
}