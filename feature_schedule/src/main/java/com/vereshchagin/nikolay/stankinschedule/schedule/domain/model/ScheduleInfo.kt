package com.vereshchagin.nikolay.stankinschedule.schedule.domain.model

import org.joda.time.DateTime

class ScheduleInfo(
    val scheduleName: String,
    var lastUpdate: DateTime? = null,
    var synced: Boolean = false,
    var position: Int = 0,
    val id: Long = 0,
)