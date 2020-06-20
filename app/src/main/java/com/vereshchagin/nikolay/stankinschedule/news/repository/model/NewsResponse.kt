package com.vereshchagin.nikolay.stankinschedule.news.repository.model

/**
 * Ответ с новостями от сервера.
 * @param success успешен ли запрос.
 * @param data данные с новостями.
 * @param error сообщение с ошибкой от сервера.
 */
class NewsResponse(
        val success: Boolean,
        val data: NewsData,
        val error: String
)


