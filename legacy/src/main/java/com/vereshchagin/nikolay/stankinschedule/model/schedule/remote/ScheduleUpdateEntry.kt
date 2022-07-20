package com.vereshchagin.nikolay.stankinschedule.model.schedule.remote

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


/**
 * Объект версии расписания в удаленном репозитории.
 */
@Entity(
    tableName = "repository_schedule_update_entries",
    foreignKeys = [
        ForeignKey(
            entity = ScheduleItemEntry::class,
            parentColumns = ["id"],
            childColumns = ["item"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class ScheduleUpdateEntry(
    @[ColumnInfo(index = true) SerializedName("item")]
    val item: Int,
    @SerializedName("id")
    val updateId: Int,
    @SerializedName("name")
    val updateName: String,
) : ScheduleRepositoryItem {

    /**
     * Уникальное ID версии.
     */
    @PrimaryKey(autoGenerate = true)
    var pk: Long = 0

    override fun data(): String = updateName
}