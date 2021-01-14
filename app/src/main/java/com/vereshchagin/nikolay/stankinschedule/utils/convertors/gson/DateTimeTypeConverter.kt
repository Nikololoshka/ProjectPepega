package com.vereshchagin.nikolay.stankinschedule.utils.convertors.gson

import com.google.gson.*
import org.joda.time.DateTime
import java.lang.reflect.Type


class DateTimeTypeConverter : JsonSerializer<DateTime?>, JsonDeserializer<DateTime?> {

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement, type: Type?, context: JsonDeserializationContext?
    ): DateTime {
        return DateTime(json.asString)
    }

    override fun serialize(
        src: DateTime?, typeOfSrc: Type?, context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}
