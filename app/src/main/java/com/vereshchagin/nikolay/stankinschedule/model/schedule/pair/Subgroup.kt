package com.vereshchagin.nikolay.stankinschedule.model.schedule.pair

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Подгруппа пары.
 */
@Parcelize
enum class Subgroup(val tag: String) : Parcelable {

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

    fun separate(subgroup: Subgroup) : Boolean {
        return this != subgroup && this != COMMON && subgroup != COMMON
    }

    companion object {
        /**
         * Возвращает значение подгруппы соотвествующие значению в строке.
         */
        fun of(value: String): Subgroup {
            for (subgroup in values()) {
                if (subgroup.tag == value) {
                    return subgroup
                }
            }
            throw IllegalArgumentException("No parse subgroup: $value")
        }
    }
}