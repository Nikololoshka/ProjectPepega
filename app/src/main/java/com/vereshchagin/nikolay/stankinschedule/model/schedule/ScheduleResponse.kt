package com.vereshchagin.nikolay.stankinschedule.model.schedule

import com.google.gson.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import java.lang.reflect.Type

/**
 * Объект ответа расписания, который возвращается от удаленного репозитория.
 */
class ScheduleResponse(
    val pairs: List<Pair>,
) {
    /**
     * Сериализатор и десериализатор объекта расписания в JSON.
     */
    class Serializer : JsonSerializer<ScheduleResponse>, JsonDeserializer<ScheduleResponse> {

        override fun serialize(
            src: ScheduleResponse?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?,
        ): JsonElement {
            if (src == null || context == null) {
                return JsonArray()
            }

            val array = JsonArray()
            for (pair in src.pairs) {
                array.add(context.serialize(pair, Pair::class.java))
            }

            return array
        }

        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?,
        ): ScheduleResponse {
            if (json == null || context == null) {
                throw JsonParseException("Schedule json is null")
            }

            val pairs = arrayListOf<Pair>()
            for (element in json.asJsonArray) {
                pairs.add(context.deserialize(element, Pair::class.java))
            }

            return ScheduleResponse(pairs)
        }
    }
}