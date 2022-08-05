package com.vereshchagin.nikolay.stankinschedule.schedule.domain.model


/**
 * Тип пары.
 */
enum class Type(val tag: String) {
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
         * Возвращает значение типа пары соответствующие значению в строке.
         */
        fun of(value: String): Type {
            for (type in values()) {
                if (type.tag.equals(value, ignoreCase = true)) {
                    return type
                }
            }
            throw IllegalArgumentException("No parse type: $value")
        }
    }
}