package com.vereshchagin.nikolay.stankinschedule.schedule.data.db.convertors

import androidx.room.TypeConverter
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.Type

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