package com.vereshchagin.nikolay.stankinschedule.model.schedule.json

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class JsonScheduleItem(
    val jsonPairs: List<JsonPairItem>,
) : Iterable<JsonPairItem> {

    class ScheduleResponseSerializer : JsonSerializer<JsonScheduleItem>,
        JsonDeserializer<JsonScheduleItem> {

        override fun serialize(
            src: JsonScheduleItem?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?,
        ): JsonElement {
            if (src == null || context == null) {
                return JsonArray()
            }

            return context.serialize(src.jsonPairs).asJsonArray
        }

        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?,
        ): JsonScheduleItem {
            if (json == null || context == null) {
                throw JsonParseException("Schedule json is null")
            }

            val type = object : TypeToken<List<JsonPairItem>>() {}.type
            return JsonScheduleItem(context.deserialize(json, type))
        }
    }

    override fun iterator(): Iterator<JsonPairItem> = jsonPairs.iterator()
}