package com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.response

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class ScheduleResponse(
    val pairs: List<PairResponse>,
) : Iterable<PairResponse> {

    class ScheduleResponseSerializer : JsonSerializer<ScheduleResponse>,
        JsonDeserializer<ScheduleResponse> {

        override fun serialize(
            src: ScheduleResponse?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?,
        ): JsonElement {
            if (src == null || context == null) {
                return JsonArray()
            }

            return context.serialize(src.pairs).asJsonArray
        }

        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?,
        ): ScheduleResponse {
            if (json == null || context == null) {
                throw JsonParseException("Schedule json is null")
            }

            val type = object : TypeToken<List<PairResponse>>() {}.type
            return ScheduleResponse(context.deserialize(json, type))
        }
    }

    override fun iterator(): Iterator<PairResponse> = pairs.iterator()
}