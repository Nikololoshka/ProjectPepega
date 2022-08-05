package com.vereshchagin.nikolay.stankinschedule.schedule.data.db

import androidx.room.*
import com.vereshchagin.nikolay.stankinschedule.schedule.data.db.convertors.PairDateConvertor
import com.vereshchagin.nikolay.stankinschedule.schedule.data.db.convertors.PairSubgroupConvertor
import com.vereshchagin.nikolay.stankinschedule.schedule.data.db.convertors.PairTimeConvertor
import com.vereshchagin.nikolay.stankinschedule.schedule.data.db.convertors.PairTypeConvertor
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.DateModel
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.Subgroup
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.Time
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.Type

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
@TypeConverters(
    PairTypeConvertor::class,
    PairSubgroupConvertor::class,
    PairTimeConvertor::class,
    PairDateConvertor::class
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
    val type: Type,
    @ColumnInfo(name = "subgroup")
    val subgroup: Subgroup,
    @ColumnInfo(name = "time")
    val time: Time,
    @ColumnInfo(name = "date")
    val date: DateModel,
) {

    /**
     * ID пары.
     */
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}