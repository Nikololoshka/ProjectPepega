package com.vereshchagin.nikolay.stankinschedule.schedule.data.api

import com.google.gson.annotations.SerializedName

class DescriptionResponse(
    @SerializedName("last_update") val lastUpdate: String,
    @SerializedName("categories_ext") val categories: List<CategoryResponse>,
) {
    class CategoryResponse(
        @SerializedName("name") val name: String,
        @SerializedName("year") val year: Int,
    )
}