package com.vereshchagin.nikolay.stankinschedule.model.schedule.pair

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Тип пары.
 */
@Parcelize
enum class Type(val tag: String) : Parcelable {
    /**
     * Лекция.
     */
    LECTURE("Lecture"),

    /**
     * Семинар.
     */
    SEMINAR("Seminar"),

    /**
     * Лабораторное занятие.
     */
    LABORATORY("Laboratory");

    companion object {
        /**
         * Возвращает значение типа пары соотвествующие значению в строке.
         */
        fun of(value: String): Type {
            for (type in values()) {
                if (type.tag == value) {
                    return type
                }
            }
            throw IllegalArgumentException("No parse type: $value")
        }
    }
}