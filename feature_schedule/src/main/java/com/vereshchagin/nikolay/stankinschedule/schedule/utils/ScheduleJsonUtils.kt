package com.vereshchagin.nikolay.stankinschedule.schedule.utils

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.*

object ScheduleJsonUtils {

    fun toJson(date: DateModel): JsonElement {
        val jsonDate = JsonArray()
        for (item in date) {
            val jsonItem: JsonElement = when (item) {
                is DateSingle -> {
                    JsonObject().apply {
                        addProperty(
                            DateItem.JSON_DATE,
                            item.toString(DateItem.JSON_DATE_PATTERN_V2)
                        )
                        addProperty(
                            DateItem.JSON_FREQUENCY,
                            item.frequency().tag
                        )
                    }
                }
                is DateRange -> {
                    JsonObject().apply {
                        addProperty(
                            DateItem.JSON_DATE,
                            item.start.toString(DateItem.JSON_DATE_PATTERN_V2) +
                                    DateItem.JSON_DATE_SEP +
                                    item.end.toString(DateItem.JSON_DATE_PATTERN_V2)
                        )
                        addProperty(
                            DateItem.JSON_FREQUENCY,
                            item.frequency().tag
                        )
                    }
                }
            }
            jsonDate.add(jsonItem)
        }

        return jsonDate
    }

    fun dateFromJson(jsonElement: JsonElement): DateModel {
        val date = DateModel()
        val dateArray = jsonElement.asJsonArray

        for (jsonDate in dateArray) {
            val json = jsonDate.asJsonObject
            val frequency = Frequency.of(json[DateItem.JSON_FREQUENCY].asString)

            if (frequency == Frequency.ONCE) {
                date.add(DateSingle(json[DateItem.JSON_DATE].asString))
            } else {
                date.add(DateRange(json[DateItem.JSON_DATE].asString, frequency))
            }
        }
        return date
    }
}