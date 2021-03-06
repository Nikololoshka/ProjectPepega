package com.vereshchagin.nikolay.stankinschedule.model.schedule.pair

import android.content.Context
import android.os.Parcelable
import com.vereshchagin.nikolay.stankinschedule.R
import kotlinx.parcelize.Parcelize

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

    fun separate(subgroup: Subgroup): Boolean {
        return this != subgroup && this != COMMON && subgroup != COMMON
    }

    override fun toString(): String {
        return tag
    }

    /**
     * Возвращает строку с учетом локализации.
     */
    fun toString(context: Context): String {
        return context.resources.getStringArray(R.array.subgroup_simple_list).getOrNull(
            listOf(A, B).indexOf(this)
        ) ?: ""
    }

    companion object {
        /**
         * Возвращает значение подгруппы соответствующие значению в строке.
         */
        @JvmStatic
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