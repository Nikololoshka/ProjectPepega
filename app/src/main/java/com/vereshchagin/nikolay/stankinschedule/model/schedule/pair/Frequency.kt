package com.vereshchagin.nikolay.stankinschedule.model.schedule.pair

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * Периодичность дат пары.
 */
@Parcelize
enum class Frequency(val tag: String, val period: Int) : Parcelable {
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