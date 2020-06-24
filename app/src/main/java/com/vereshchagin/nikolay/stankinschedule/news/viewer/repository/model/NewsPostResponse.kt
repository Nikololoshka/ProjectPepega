package com.vereshchagin.nikolay.stankinschedule.news.viewer.repository.model


class NewsPostResponse(
    val success: Boolean,
    val data: NewsPostData,
    val error: String
)