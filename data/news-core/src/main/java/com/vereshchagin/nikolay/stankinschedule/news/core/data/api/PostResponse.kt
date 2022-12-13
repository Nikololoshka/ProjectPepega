package com.vereshchagin.nikolay.stankinschedule.news.core.data.api

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

class PostResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: NewsPost,
    @SerializedName("error") val error: String,
) {
    /**
     * Новостной пост.
     *
     * @param id номер поста.
     * @param datetime дата и время поста.
     * @param title заголовок новости.
     * @param logo относительный путь к картинке новости.
     * @param text текст новости.
     * @param delta разметка новости.
     */
    class NewsPost(
        val id: Int,
        val datetime: String,
        val title: String,
        val logo: String,
        val text: String,
        val delta: String,
    )

    /**
     * Десериализатор объекта NewsPost из JSON.
     */
    class NewsPostDeserializer : JsonDeserializer<NewsPost> {
        override fun deserialize(
            json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?,
        ): NewsPost {
            val rootObject =
                json?.asJsonObject ?: throw JsonParseException("Response is not JSON object")

            val id = rootObject.get("id")?.asInt
            val date = rootObject.get("date")?.asString
            val title = rootObject.get("title")?.asString
            val logo = rootObject.get("logo")?.asString

            var text = ""
            try {
                val textObject = rootObject.get("text")
                if (textObject != null && !textObject.isJsonNull) {
                    text = rootObject.get("text").asString
                }
            } catch (ignored: Exception) {

            }

            if (id == null || date == null || title == null || logo == null) {
                throw JsonParseException("JSON object has empty attributes")
            }

            val delta = (find(rootObject, "ops")?.toString() ?: "")
                // замена относительных путей файлов на абсолютные
                .replace(Regex("(/uploads.+?)\"")) { result: MatchResult ->
                    StankinNewsAPI.BASE_URL + result.value
                }

            return NewsPost(
                id,
                date,
                title,
                logo,
                text,
                delta
            )
        }

        /**
         * Рекурсивный поиск элемента.
         *
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