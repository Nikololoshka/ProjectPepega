package com.vereshchagin.nikolay.stankinschedule.schedule.core.data.api

import com.google.gson.annotations.SerializedName

data class PairJson(
    @SerializedName("title") val title: String,
    @SerializedName("lecturer") val lecturer: String,
    @SerializedName("classroom") val classroom: String,
    @SerializedName("type") val type: String,
    @SerializedName("subgroup") val subgroup: String,
    @SerializedName("time") val time: TimeJson,
    @SerializedName("dates") val date: List<DateJson>,
) {
    data class TimeJson(
        @SerializedName("start") val start: String,
        @SerializedName("end") val end: String,
    )

    data class DateJson(
        @SerializedName("frequency") val frequency: String,
        @SerializedName("date") val date: String,
    )
}