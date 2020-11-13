package com.vereshchagin.nikolay.stankinschedule.model.news

import com.google.gson.annotations.SerializedName


class NewsPostResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: NewsPost,
    @SerializedName("error")
    val error: String
)