package com.vereshchagin.nikolay.stankinschedule.model.schedule.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Сущность расписания в БД.
 */
@Entity(
    tableName = "schedules",
    indices = [
        Index("schedule_name", unique = true)
    ]
)
data class ScheduleItem(
    @ColumnInfo(name = "schedule_name")
    var scheduleName: String,
) {
    /**
     * ID расписания.
     */
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    /**
     * Будет ли расписание синхронизироваться с репозиторием.
     */
    var synchronized: Boolean = false

    /**
     * Порядковый номер в списке.
     */
    var position: Int = 0
}