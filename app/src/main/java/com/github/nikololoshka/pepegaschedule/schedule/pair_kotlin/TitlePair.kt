package com.github.nikololoshka.pepegaschedule.schedule.pair_kotlin

import org.json.JSONObject

/**
 * Класс названия предмета пары.
 */
class DisciplinePair : AttributePair() {

    val DISCIPLINE_JSON = "title"

    /**
     * Название предмета.
     */
    var discipline: String = ""

    override fun load(jsonObject: JSONObject) {
        discipline = jsonObject.getString(DISCIPLINE_JSON)
    }

    override fun save(jsonObject: JSONObject) {
        jsonObject.put(DISCIPLINE_JSON, discipline)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DisciplinePair

        if (discipline != other.discipline) return false

        return true
    }

    override fun hashCode(): Int {
        return discipline.hashCode()
    }
}