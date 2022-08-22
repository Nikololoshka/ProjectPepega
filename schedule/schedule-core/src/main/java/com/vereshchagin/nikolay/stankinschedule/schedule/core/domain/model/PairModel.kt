package com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model


/**
 * Пара в расписании.
 *
 * @param title название пары.
 * @param lecturer преподаватель.
 * @param classroom аудитория.
 * @param type тип пары.
 * @param subgroup подгруппа пары.
 * @param time время пары.
 * @param date даты проведения пары.
 */
data class PairModel(
    val title: String,
    val lecturer: String,
    val classroom: String,
    val type: Type,
    val subgroup: Subgroup,
    val time: Time,
    val date: DateModel,
) : Comparable<PairModel> {

    /**
     * Определяет, пересекаются ли пары по времени, дате и подгруппе.
     * @param other другая пара.
     */
    fun isIntersect(other: PairModel): Boolean {
        return time.isIntersect(other.time) &&
                date.intersect(other.date) &&
                subgroup.isIntersect(other.subgroup)
    }

    /**
     * Возвращает true, если пара может быть у этой подгруппы, иначе false.
     */
    fun isCurrently(subgroup: Subgroup): Boolean {
        return this.subgroup == Subgroup.COMMON ||
                subgroup == Subgroup.COMMON ||
                this.subgroup == subgroup
    }

    override fun compareTo(other: PairModel): Int {
        if (time.start == other.time.start) {
            return subgroup.compareTo(other.subgroup)
        }
        return time.start.compareTo(other.time.start)
    }

    override fun toString(): String {
        return "$title. $lecturer. $classroom. $type. $subgroup. $time. $date"
    }
}