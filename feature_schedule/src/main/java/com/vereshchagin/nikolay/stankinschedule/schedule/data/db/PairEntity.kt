package com.vereshchagin.nikolay.stankinschedule.schedule.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Пара в расписании для хранения в БД.
 */
@Entity(
    tableName = "schedule_pair_entities",
    foreignKeys = [
        ForeignKey(
            entity = ScheduleEntity::class,
            parentColumns = ["id"],
            childColumns = ["schedule_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PairEntity(
    @ColumnInfo(name = "schedule_id", index = true)
    val scheduleId: Long,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "lecturer")
    val lecturer: String,
    @ColumnInfo(name = "classroom")
    val classroom: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "subgroup")
    val subgroup: String,
    @ColumnInfo(name = "time")
    val time: String,
    @ColumnInfo(name = "date")
    val date: String,
) {

    /**
     * ID пары.
     */
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}