package com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model

/**
 * Подгруппа пары.
 */
enum class Subgroup(val tag: String) {

    /**
     * Подгруппа А.
     */
    A("A"),

    /**
     * Подгруппа Б.
     */
    B("B"),

    /**
     * Без подгруппы.
     */
    COMMON("Common");

    /**
     * Определяет, пересекаются ли подгруппы в расписании.
     */
    fun isIntersect(subgroup: Subgroup): Boolean {
        return this == subgroup || this == COMMON || subgroup == COMMON
    }

    /**
     * Определяет, должна ли подгруппа отображаться в расписании.
     */
    fun isShow(): Boolean {
        return this != COMMON
    }

    override fun toString(): String {
        return tag
    }

    companion object {
        /**
         * Возвращает значение подгруппы соответствующие значению в строке.
         */
        @JvmStatic
        fun of(value: String): Subgroup {
            for (subgroup in values()) {
                if (subgroup.tag.equals(value, ignoreCase = true)) {
                    return subgroup
                }
            }
            throw IllegalArgumentException("No parse subgroup: $value")
        }
    }
}