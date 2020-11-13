package com.vereshchagin.nikolay.stankinschedule.model.news

import com.google.gson.annotations.SerializedName

/**
 * Ответ с новостями от сервера.
 * @param success успешен ли запрос.
 * @param data данные с новостями.
 * @param error сообщение с ошибкой от сервера.
 */
class NewsResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: NewsData,
    @SerializedName("error")
    val error: String
)


