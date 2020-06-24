package com.vereshchagin.nikolay.stankinschedule.news.review.categories.repository.model

/**
 * Данные новостей запроса.
 * @param news массив с новостями.
 * @param count количество полученных новостей.
 */
class NewsData(
        val news: List<NewsPost>,
        val count: Int
)