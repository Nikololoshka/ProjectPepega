package com.vereshchagin.nikolay.stankinschedule.news.viewer.repository.model


class NewsPostResponse(
    val success: Boolean,
    val data: NewsPost,
    val error: String
)