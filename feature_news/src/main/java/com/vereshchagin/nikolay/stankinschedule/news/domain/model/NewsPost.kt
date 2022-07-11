package com.vereshchagin.nikolay.stankinschedule.news.domain.model

data class NewsPost(
    val id: Int,
    val title: String,
    val previewImageUrl: String?,
    val date: String,
)