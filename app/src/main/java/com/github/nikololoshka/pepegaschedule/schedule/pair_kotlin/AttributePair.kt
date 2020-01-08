package com.github.nikololoshka.pepegaschedule.schedule.pair_kotlin

import org.json.JSONException
import org.json.JSONObject

/**
 * Базовый абстрактный класс для всех атрибутов пары.
 */
abstract class AttributePair {

    /**
     * Загружает атрибут из json файла.
     * @param jsonObject json объект с парой.
     */
    @Throws(JSONException::class)
    abstract fun load(jsonObject: JSONObject)

    /**
     * Сохраняет атрибут в json файл.
     * @param jsonObject json объект с парой.
     */
    @Throws(JSONException::class)
    abstract fun save(jsonObject: JSONObject)
}