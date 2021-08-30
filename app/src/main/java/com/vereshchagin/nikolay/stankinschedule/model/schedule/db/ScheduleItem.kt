package com.vereshchagin.nikolay.stankinschedule.model.schedule.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.joda.time.DateTime

/**
 * Сущность расписания в БД.
 * @param scheduleName название расписания.
 */
@Entity(
    tableName = "schedule_items",
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
     * Последнее обновление расписания.
     * Используется для функции синхронизации.
     */
    @ColumnInfo(name = "last_update")
    var lastUpdate: DateTime? = null

    /**
     * Будет ли расписание синхронизироваться с репозиторием.
     */
    @ColumnInfo(name = "synced")
    var synced: Boolean = false

    /**
     * Порядковый номер в списке.
     */
    @ColumnInfo(name = "position")
    var position: Int = 0
}