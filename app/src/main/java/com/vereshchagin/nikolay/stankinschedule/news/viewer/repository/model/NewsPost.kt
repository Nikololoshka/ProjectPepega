package com.vereshchagin.nikolay.stankinschedule.news.viewer.repository.model

import com.google.gson.*
import com.vereshchagin.nikolay.stankinschedule.news.viewer.repository.HtmlGenerator
import java.lang.reflect.Type


/**
 *
 */
class NewsPost(
    val id: Int,
    val date: String,
    val title: String,
    val logo: String,
    val text: String,
    val html: String
) {

    companion object {
        /**
         * Десериализатор объекта NewsPost из JSON.
         */
        class NewsPostDeserializer: JsonDeserializer<NewsPost> {
            override fun deserialize(
                json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?
            ): NewsPost {
                val rootObject = json?.asJsonObject ?:
                    throw JsonParseException("Response is not JSON object")

                val id = rootObject.get("id")?.asInt
                val date = rootObject.get("date")?.asString
                val title = rootObject.get("title")?.asString
                val logo = rootObject.get("logo")?.asString
                val text = rootObject.get("text")?.asString

                if (id == null || date == null || title == null || logo == null || text == null) {
                    throw JsonParseException("JSON object has empty attributes")
                }

                val rawText = find(rootObject, "ops")
                val html = if (rawText != null) {
                    HtmlGenerator.generate(rawText.asJsonArray)
                } else {
                    ""
                }

                return NewsPost(id, date, title, logo, text, html)
            }

            /**
             * Рекурсивный поиск элемента.
             * @param root начала поиска.
             * @param key необходимый элемент.
             * @param treeKey элемент, по котору дальше продолжает поиск.
             * @return JSON объект нужного элемента. Возвращает null если не найден.
             */
            private fun find(root: JsonObject, key: String, treeKey: String = "delta"): JsonElement? {
                if (root.has(key)) {
                    return root.get(key)
                }
                if (root.has(treeKey)) {
                    return find(root.get(treeKey).asJsonObject, key, treeKey)
                }
                return null
            }
        }
    }
}
