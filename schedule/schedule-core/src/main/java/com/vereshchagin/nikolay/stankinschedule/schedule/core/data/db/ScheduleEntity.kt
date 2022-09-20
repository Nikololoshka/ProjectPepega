package com.vereshchagin.nikolay.stankinschedule.schedule.core.data.db

import androidx.room.*
import org.joda.time.DateTime

/**
 * Сущность расписания в БД.
 * @param scheduleName название расписания.
 */
@Entity(
    tableName = "schedule_entities",
    indices = [
        Index("schedule_name", unique = true)
    ]
)
@TypeConverters(DateTimeConverter::class)
class ScheduleEntity(
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

    fun copy(
        scheduleName: String = this.scheduleName,
        id: Long = this.id,
        lastUpdate: DateTime? = this.lastUpdate,
        synced: Boolean = this.synced,
        position: Int = this.position
    ): ScheduleEntity = ScheduleEntity(scheduleName).apply {
        this.id = id
        this.lastUpdate = lastUpdate
        this.synced = synced
        this.position = position
    }
}