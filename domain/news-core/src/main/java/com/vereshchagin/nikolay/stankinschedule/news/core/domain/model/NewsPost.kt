package com.vereshchagin.nikolay.stankinschedule.news.core.domain.model

data class NewsPost(
    val id: Int,
    val title: String,
    val previewImageUrl: String?,
    val date: String,
)