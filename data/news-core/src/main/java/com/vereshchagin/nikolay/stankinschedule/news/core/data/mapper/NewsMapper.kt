package com.vereshchagin.nikolay.stankinschedule.news.core.data.mapper

import com.vereshchagin.nikolay.stankinschedule.news.core.data.api.NewsResponse
import com.vereshchagin.nikolay.stankinschedule.news.core.data.api.PostResponse
import com.vereshchagin.nikolay.stankinschedule.news.core.data.api.StankinNewsAPI
import com.vereshchagin.nikolay.stankinschedule.news.core.data.db.NewsEntity
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsContent
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsPost


fun NewsResponse.NewsData.NewsItem.toPost(): NewsPost {
    return NewsPost(
        id = id,
        title = title,
        previewImageUrl = StankinNewsAPI.BASE_URL + logo,
        date = date.split(" ").first()
    )
}

fun NewsPost.toEntity(
    index: Int,
    newsSubdivision: Int,
): NewsEntity {
    return NewsEntity(
        id = id,
        indexOrder = index,
        newsSubdivision = newsSubdivision,
        title = title,
        date = date,
        logo = previewImageUrl ?: ""
    )
}

fun PostResponse.NewsPost.toNewsContent(): NewsContent {
    return NewsContent(
        id = id,
        date = datetime.split(" ").first(),
        title = title,
        previewImageUrl = StankinNewsAPI.BASE_URL + logo,
        text = text,
        deltaFormat = delta
    )
}

fun NewsEntity.toPost(): NewsPost {
    return NewsPost(
        id = id,
        title = title,
        previewImageUrl = logo,
        date = date
    )
}
