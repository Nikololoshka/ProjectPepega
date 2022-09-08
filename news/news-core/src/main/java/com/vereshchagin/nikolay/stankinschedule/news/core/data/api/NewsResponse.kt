package com.vereshchagin.nikolay.stankinschedule.news.core.data.api

import com.google.gson.annotations.SerializedName

/**
 * Ответ с новостями от сервера.
 * @param success успешен ли запрос.
 * @param data данные с новостями.
 * @param error сообщение с ошибкой от сервера.
 */
data class NewsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: NewsData,
    @SerializedName("error") val error: String,
) {
    /**
     * Данные новостей запроса.
     * @param news массив с новостями.
     * @param count количество полученных новостей.
     */
    data class NewsData(
        @SerializedName("news") val news: List<NewsItem>,
        @SerializedName("count") val count: Int,
    ) {
        /**
         * Данные о новости.
         * @param id номер новости.
         * @param title заголовок.
         * @param date дата публикации новости.
         * @param logo относительный путь к картинке новости.
         * @param shortText короткий текст новости.
         * @param author номер автора новости.
         */
        data class NewsItem(
            @SerializedName("id") val id: Int,
            @SerializedName("title") val title: String,
            @SerializedName("date") val date: String,
            @SerializedName("logo") val logo: String,
            @SerializedName("short_text") val shortText: String,
            @SerializedName("author_id") val author: Int,
        )
    }
}


