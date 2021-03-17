package com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1

import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryItem
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.room.ScheduleVersionConverter

@Entity(
    tableName = "schedule_entries",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntry::class,
            parentColumns = ["id"],
            childColumns = ["category"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@TypeConverters(ScheduleVersionConverter::class)
class ScheduleEntry(
    @SerializedName("category")
    @ColumnInfo(index = true)
    val category: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("paths")
    val paths: List<String>,
    @SerializedName("versions")
    val versions: List<ScheduleVersion>,
) : RepositoryItem {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    override fun data() = name

}