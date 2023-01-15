package com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model


/**
 * Периодичность дат пары.
 */
enum class Frequency(val tag: String, val period: Int) {
    /**
     * Единожды.
     */
    ONCE("once", 1),

    /**
     * Каждую неделю.
     */
    EVERY("every", 7),

    /**
     * Через неделю.
     */
    THROUGHOUT("throughout", 14);

    companion object {

        /**
         * Возвращает периодичность по строке.
         */
        fun of(value: String): Frequency {
            for (frequency in values()) {
                if (frequency.tag == value) {
                    return frequency
                }
            }
            throw IllegalArgumentException("No parse frequency: $value")
        }
    }
}