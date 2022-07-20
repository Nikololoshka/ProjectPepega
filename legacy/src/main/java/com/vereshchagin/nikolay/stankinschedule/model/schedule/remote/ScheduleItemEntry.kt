package com.vereshchagin.nikolay.stankinschedule.model.schedule.remote

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import org.joda.time.Hours

/**
 * Объект расписания с версиями в удаленном репозитории.
 */
@Entity(
    tableName = "repository_schedule_item_entries",
    foreignKeys = [
        ForeignKey(
            entity = ScheduleCategoryEntry::class,
            parentColumns = ["id"],
            childColumns = ["category"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class ScheduleItemEntry(
    @[PrimaryKey SerializedName("id")]
    var id: Int = 0,
    @[ColumnInfo(index = true) SerializedName("category")]
    val category: Int,
    @SerializedName("name")
    val name: String,
) : ScheduleRepositoryItem {

    var date: DateTime? = null

    fun isValid(): Boolean {
        return if (date == null) false else Hours.hoursBetween(date, DateTime.now()).hours < 24
    }

    override fun data() = name
}