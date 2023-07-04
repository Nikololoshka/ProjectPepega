package com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.components

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.DateModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.DateRange
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.DateSingle
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Frequency
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.PairModel
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Subgroup
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Type

class PairFormatter {

    fun format(pair: PairModel): String {
        return buildString {
            append(pair.title)
            append(". ")

            append(pair.lecturer)
            append(". ")

            append(pairType(pair.type))
            append(". ")

            val subgroupText = pairSubgroup(pair.subgroup)
            if (subgroupText != null) {
                append(subgroupText)
                append(". ")
            }

            append(pairDate(pair.date))
        }
    }

    private fun pairType(type: Type): String {
        return when (type) {
            Type.LECTURE -> "Лекция"
            Type.SEMINAR -> "Семинар"
            Type.LABORATORY -> "Лабораторные занятия"
        }
    }


    private fun pairSubgroup(subgroup: Subgroup): String? {
        return when (subgroup) {
            Subgroup.COMMON -> return null
            Subgroup.A -> "(А)"
            Subgroup.B -> "(Б)"
        }
    }


    private fun pairDate(date: DateModel): String {
        return "[" +
                date.joinToString(
                    separator = ", ",
                    transform = { item ->
                        when (item) {
                            is DateSingle -> {
                                item.toString("dd.MM")
                            }

                            is DateRange -> {
                                item.toString("dd.MM", "-") + " " +
                                        pairDateFrequency(item.frequency())
                            }
                        }
                    }
                ) +
                "]"
    }

    private fun pairDateFrequency(frequency: Frequency): String {
        return when (frequency) {
            Frequency.ONCE -> return ""
            Frequency.EVERY -> "к.н."
            Frequency.THROUGHOUT -> "ч.н."
        }
    }
}





