package com.vereshchagin.nikolay.stankinschedule.utils.convertors.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Конвертор списка для Room DB.
 */
class ListConverter {

    @TypeConverter
    public fun fromList(list: List<String>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    public fun toList(json: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(json, type)
    }
}