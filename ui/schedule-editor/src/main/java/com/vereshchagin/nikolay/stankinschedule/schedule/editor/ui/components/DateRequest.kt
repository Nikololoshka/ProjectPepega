package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui.components

import androidx.annotation.StringRes
import org.joda.time.LocalDate

class DateRequest(
    @StringRes val title: Int,
    val selectedDate: LocalDate,
    val id: String,
)