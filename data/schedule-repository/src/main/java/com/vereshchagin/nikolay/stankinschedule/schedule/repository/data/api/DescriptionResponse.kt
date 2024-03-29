package com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.api

import com.google.gson.annotations.SerializedName

data class DescriptionResponse(
    @SerializedName("last_update") val lastUpdate: String,
    @SerializedName("categories_ext") val categories: List<CategoryResponse>,
) {
    data class CategoryResponse(
        @SerializedName("name") val name: String,
        @SerializedName("year") val year: Int,
    )
}