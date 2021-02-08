package com.vereshchagin.nikolay.stankinschedule.model.modulejournal

/**
 * Типы оценок в модульном журнале.
 */
enum class MarkType(val tag: String, val weight: Int) {

    /**
     * Первый модуль.
     */
    FIRST_MODULE("М1", 3),

    /**
     * Второй модуль.
     */
    SECOND_MODULE("М2", 2),

    /**
     * Курсовая работа.
     */
    COURSEWORK("К", 5),

    /**
     * Зачёт.
     */
    CREDIT("З", 5),

    /**
     * Экзамен.
     */
    EXAM("Э", 7);

    override fun toString(): String {
        return tag
    }

    companion object {
        /**
         * Возвращает тип оценки из перечисления соответствующего значению ответа от сервера.
         */
        @JvmStatic
        fun of(value: String?): MarkType {
            for (type in values()) {
                if (type.tag == value) {
                    return type
                }
            }
            throw IllegalArgumentException("Unknown mark type: '$value'")
        }
    }
}