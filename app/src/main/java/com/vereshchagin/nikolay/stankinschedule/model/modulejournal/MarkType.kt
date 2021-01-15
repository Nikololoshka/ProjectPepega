package com.vereshchagin.nikolay.stankinschedule.model.modulejournal

import com.google.gson.annotations.SerializedName

/**
 * Типы оценок в модульном журнале.
 */
enum class MarkType(val tag: String, val weight: Int) {

    /**
     * Первый модуль.
     */
    @SerializedName("FIRST_MODULE")
    FIRST_MODULE("М1", 3),

    /**
     * Второй модуль.
     */
    @SerializedName("SECOND_MODULE")
    SECOND_MODULE("М2", 2),

    /**
     * Курсовая работа.
     */
    @SerializedName("COURSEWORK")
    COURSEWORK("К", 5),

    /**
     * Зачёт.
     */
    @SerializedName("CREDIT")
    CREDIT("З", 5),

    /**
     * Экзамен.
     */
    @SerializedName("EXAM")
    EXAM("Э", 7);

    companion object {
        /**
         * Возвращает тип оценки из перечисления соответствующего значению ответа от сервера.
         */
        fun of(value: String): MarkType {
            for (type in values()) {
                if (type.tag == value) {
                    return type
                }
            }
            throw IllegalArgumentException("Unknown mark type: $value")
        }
    }
}