package com.vereshchagin.nikolay.stankinschedule.model.schedule.remote

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import org.joda.time.Hours

/**
 * Объект категории в удаленном репозитории.
 */
@Entity(tableName = "repository_category_entries")
class ScheduleCategoryEntry(
    @[PrimaryKey SerializedName("id")]
    val id: Int,
    @SerializedName("parent")
    val parent: Int?,
    @SerializedName("name")
    val name: String,
    @SerializedName("is_node")
    val isNode: Boolean,
    @ColumnInfo(name = "time")
    val time: DateTime? = null,
) : ScheduleRepositoryItem {

    constructor(
        entry: ScheduleCategoryEntry, updateTime: DateTime,
    ) : this(entry.id, entry.parent, entry.name, entry.isNode, updateTime)

    fun isValid(): Boolean {
        return if (time == null) false else Hours.hoursBetween(time, DateTime.now()).hours < 24
    }

    override fun data() = name
}