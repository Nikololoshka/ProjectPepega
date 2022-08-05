package com.vereshchagin.nikolay.stankinschedule.schedule.data.db.convertors

import androidx.room.TypeConverter
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.Subgroup

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