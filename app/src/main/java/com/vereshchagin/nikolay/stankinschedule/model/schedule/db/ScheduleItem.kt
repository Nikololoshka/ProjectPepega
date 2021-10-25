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
open class ScheduleItem(
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


    constructor(item: ScheduleItem) : this(item.scheduleName) {
        id = item.id
        lastUpdate = item.lastUpdate
        synced = item.synced
        position = item.position
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ScheduleItem) return false

        if (scheduleName != other.scheduleName) return false
        if (id != other.id) return false
        if (lastUpdate != other.lastUpdate) return false
        if (synced != other.synced) return false
        if (position != other.position) return false

        return true
    }

    override fun hashCode(): Int {
        var result = scheduleName.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + (lastUpdate?.hashCode() ?: 0)
        result = 31 * result + synced.hashCode()
        result = 31 * result + position
        return result
    }
}