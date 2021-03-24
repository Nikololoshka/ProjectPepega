package com.vereshchagin.nikolay.stankinschedule.model.schedule.db

import androidx.room.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.*
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.room.PairDateConvertor
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.room.PairSubgroupConvertor
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.room.PairTimeConvertor
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.room.PairTypeConvertor

/**
 * Пара в расписании.
 */
@Entity(
    tableName = "pairs",
    foreignKeys = [
        ForeignKey(
            entity = ScheduleItem::class,
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
class PairItem(
    @ColumnInfo(name = "schedule_id", index = true)
    var scheduleId: Long,
    title: String,
    lecturer: String,
    classroom: String,
    type: Type,
    subgroup: Subgroup,
    time: Time,
    date: Date,
) : Pair(title, lecturer, classroom, type, subgroup, time, date) {

    /**
     * ID пары.
     */
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}