package com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model

import org.joda.time.DateTime

class ScheduleInfo(
    val scheduleName: String,
    var lastUpdate: DateTime? = null,
    var synced: Boolean = false,
    var position: Int = 0,
    val id: Long = 0,
) {
    fun copy(
        scheduleName: String = this.scheduleName,
        lastUpdate: DateTime? = this.lastUpdate,
        synced: Boolean = this.synced,
        position: Int = this.position,
    ): ScheduleInfo {
        return ScheduleInfo(
            scheduleName = scheduleName,
            lastUpdate = lastUpdate,
            synced = synced,
            position = position,
            id = id
        )
    }
}