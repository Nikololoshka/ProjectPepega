package com.vereshchagin.nikolay.stankinschedule.model.news

import com.google.gson.annotations.SerializedName

/**
 * Данные новостей запроса.
 * @param news массив с новостями.
 * @param count количество полученных новостей.
 */
class NewsData(
    @SerializedName("news")
    val news: List<NewsItem>,
    @SerializedName("count")
    val count: Int,
)