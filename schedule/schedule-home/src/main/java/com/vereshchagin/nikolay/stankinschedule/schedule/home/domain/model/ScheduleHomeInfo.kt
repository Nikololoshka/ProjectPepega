package com.vereshchagin.nikolay.stankinschedule.schedule.home.domain.model

import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.data.ScheduleViewDay

class ScheduleHomeInfo(
    val scheduleName: String,
    val scheduleId: Long,
    val days: List<ScheduleViewDay>
)