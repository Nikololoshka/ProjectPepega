package com.vereshchagin.nikolay.stankinschedule.utils.convertors.room

import androidx.room.TypeConverter
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Type

class PairTypeConvertor {

    @TypeConverter
    fun toType(type: String): Type {
        return Type.of(type)
    }

    @TypeConverter
    fun fromType(type: Type): String {
        return type.tag
    }
}