package com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1

import androidx.paging.PagingData
import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.room.ScheduleVersionConverter
import org.joda.time.LocalDate

/**
 * Объект расписания с версиями в удаленном репозитории.
 */
@Entity(
    tableName = "repository_schedule_entries",
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
    @SerializedName("versions")
    val versions: List<ScheduleVersion>,
) : RepositoryItem {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    override fun data() = name

    /**
     * Возвращает PagingData версий расписания для загрузки.
     */
    fun versionEntries(): PagingData<ScheduleVersionEntry> {
        return PagingData.from(
            versions
                .sortedByDescending { LocalDate.parse(it.date) }
                .map { version -> ScheduleVersionEntry(name, version.path, version.date) }
        )
    }
}