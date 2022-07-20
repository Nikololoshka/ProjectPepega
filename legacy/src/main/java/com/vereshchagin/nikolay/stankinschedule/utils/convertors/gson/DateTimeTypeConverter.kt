package com.vereshchagin.nikolay.stankinschedule.utils.convertors.gson

import com.google.gson.*
import org.joda.time.DateTime
import java.lang.reflect.Type

/**
 * Конвертор для Gson класса DateTime.
 */
class DateTimeTypeConverter : JsonSerializer<DateTime>, JsonDeserializer<DateTime> {

    override fun serialize(
        src: DateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?,
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        type: Type?,
        context:
        JsonDeserializationContext?,
    ): DateTime {
        return DateTime(json.asString)
    }
}
