package com.vereshchagin.nikolay.stankinschedule.news.repository.network

/**
 * Данные для POST запроса к API.
 */
class StankinPostData (
    val action: String,
    val data: Map<String, Any>
)