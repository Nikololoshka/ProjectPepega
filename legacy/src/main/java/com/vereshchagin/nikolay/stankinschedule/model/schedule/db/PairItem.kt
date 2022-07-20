package com.vereshchagin.nikolay.stankinschedule.model.schedule.db

import androidx.room.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.json.JsonPairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Date
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Time
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Type
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
data class PairItem(
    @ColumnInfo(name = "schedule_id", index = true)
    val scheduleId: Long,
    val title: String,
    val lecturer: String,
    val classroom: String,
    val type: Type,
    val subgroup: Subgroup,
    val time: Time,
    val date: Date,
) : Comparable<PairItem> {

    /**
     * ID пары.
     */
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    /**
     * Конструктор пары из json.
     */
    constructor(
        scheduleId: Long,
        jsonResponse: JsonPairItem,
    ) : this(
        scheduleId,
        jsonResponse.title,
        jsonResponse.lecturer,
        jsonResponse.classroom,
        Type.of(jsonResponse.type),
        Subgroup.of(jsonResponse.subgroup),
        Time(jsonResponse.time),
        Date(jsonResponse.dates)
    )

    constructor(
        title: String,
        lecturer: String,
        classroom: String,
        type: Type,
        subgroup: Subgroup,
        time: Time,
        date: Date,
    ) : this(
        -1L,
        title,
        lecturer,
        classroom,
        type,
        subgroup,
        time,
        date
    )

    constructor(
        scheduleId: Long,
        pair: PairItem,
    ) : this(
        scheduleId,
        pair.title,
        pair.lecturer,
        pair.classroom,
        pair.type,
        pair.subgroup,
        pair.time,
        pair.date
    )

    /**
     * Определяет, пересекаются ли пары по времени, дате и подгруппе.
     * @param other другая пара.
     */
    fun isIntersect(other: PairItem): Boolean {
        return time.isIntersect(other.time) && date.intersect(other.date) &&
                subgroup.isIntersect(other.subgroup)
    }

    /**
     * Возвращает true, если пара может быть у этой подгруппы, иначе false.
     */
    fun isCurrently(subgroup: Subgroup): Boolean {
        return this.subgroup == Subgroup.COMMON || subgroup == Subgroup.COMMON || this.subgroup == subgroup
    }

    override fun compareTo(other: PairItem): Int {
        if (time.start == other.time.start) {
            return subgroup.compareTo(other.subgroup)
        }
        return time.start.compareTo(other.time.start)
    }

    fun equalData(
        otherTitle: String,
        otherLecturer: String,
        otherClassroom: String,
        otherType: Type,
        otherSubgroup: Subgroup,
        otherTime: Time,
        otherDate: Date,
    ): Boolean {
        if (title != otherTitle) return false
        if (lecturer != otherLecturer) return false
        if (classroom != otherClassroom) return false
        if (type != otherType) return false
        if (subgroup != otherSubgroup) return false
        if (time != otherTime) return false
        if (date != otherDate) return false

        return true
    }

    override fun toString(): String {
        return "$title. $lecturer. $classroom. $type. $subgroup. $time. $date"
    }
}