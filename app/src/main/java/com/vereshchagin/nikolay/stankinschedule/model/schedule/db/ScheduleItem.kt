package com.vereshchagin.nikolay.stankinschedule.model.schedule.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.joda.time.DateTime

/**
 *
 */
@Entity(tableName = "schedules", indices = [Index("schedule_name", unique = true)])
data class ScheduleItem(
    @ColumnInfo(name = "schedule_name")
    var scheduleName: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    /**
     *
     */
    @ColumnInfo(name = "created_date")
    var createdDate: DateTime = DateTime.now()

    /**
     *
     */
    @ColumnInfo(name = "edit_date")
    var editDate: DateTime = DateTime.now()

    /**
     *
     */
    var synchronized: Boolean = false

    /**
     *
     */
    var position: Int = 0
}