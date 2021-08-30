package com.vereshchagin.nikolay.stankinschedule.model.schedule.db

import androidx.room.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.*
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.room.PairDateConvertor
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.room.PairSubgroupConvertor
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.room.PairTimeConvertor
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.room.PairTypeConvertor

/**
 * Пара в расписании для хранения в БД.
 * @param scheduleId
 * @param title название пары.
 * @param lecturer преподаватель.
 * @param classroom аудитория.
 * @param type тип пары.
 * @param subgroup подгруппа пары.
 * @param time время пары.
 * @param date даты проведения пары.
 */
@Entity(
    tableName = "schedule_pairs",
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

    companion object {
        /**
         * Создает PairItem из обычной Pair для использования в БД.
         * @param scheduleId ID расписания в БД, к которому принадлежит пара.
         * @param pair пара.
         * @param pairId ID пары.
         */
        @JvmStatic
        fun from(scheduleId: Long, pair: Pair, pairId: Long = 0) : PairItem {
            return PairItem(
                scheduleId,
                pair.title,
                pair.lecturer,
                pair.classroom,
                pair.type,
                pair.subgroup,
                pair.time,
                pair.date
            ).apply {
                id = pairId
            }
        }
    }
}