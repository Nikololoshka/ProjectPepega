package com.vereshchagin.nikolay.stankinschedule.model.news

/**
 * Данные новостей запроса.
 * @param news массив с новостями.
 * @param count количество полученных новостей.
 */
class NewsData(
    val news: List<NewsItem>,
    val count: Int
)