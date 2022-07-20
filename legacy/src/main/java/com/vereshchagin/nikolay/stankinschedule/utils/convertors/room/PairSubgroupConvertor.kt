package com.vereshchagin.nikolay.stankinschedule.utils.convertors.room

import androidx.room.TypeConverter
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup

class PairSubgroupConvertor {

    @TypeConverter
    fun toSubgroup(subgroup: String): Subgroup {
        return Subgroup.of(subgroup)
    }

    @TypeConverter
    fun fromSubgroup(subgroup: Subgroup): String {
        return subgroup.tag
    }
}