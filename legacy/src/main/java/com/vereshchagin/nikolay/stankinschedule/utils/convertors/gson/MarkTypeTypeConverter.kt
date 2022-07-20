package com.vereshchagin.nikolay.stankinschedule.utils.convertors.gson

import com.google.gson.*
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.MarkType
import java.lang.reflect.Type

/**
 * Конвертор для Gson класса MarkType.
 */
class MarkTypeTypeConverter : JsonSerializer<MarkType>, JsonDeserializer<MarkType> {

    override fun serialize(
        src: MarkType?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?,
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): MarkType {
        return MarkType.of(json.asString)
    }
}