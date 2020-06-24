package com.vereshchagin.nikolay.stankinschedule.news.viewer.repository.model

import com.google.gson.annotations.SerializedName

class NewsPostData(
    val id: Int,
    val date: String,
    val title: String,
    @SerializedName("short_text")
    val shortText: String,
    val logo: String,
    val text: String,
    val author_id : Int,
    val subdivision_id : Int,
    val pull_site : Boolean,
    val is_main : Boolean
)
