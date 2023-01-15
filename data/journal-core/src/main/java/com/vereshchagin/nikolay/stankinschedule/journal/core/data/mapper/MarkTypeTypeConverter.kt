package com.vereshchagin.nikolay.stankinschedule.journal.core.data.mapper

import com.google.gson.*
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.MarkType
import java.lang.reflect.Type

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