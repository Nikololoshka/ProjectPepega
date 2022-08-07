package com.vereshchagin.nikolay.stankinschedule.schedule.data.api

import com.google.gson.annotations.SerializedName

class PairResponse(
    @SerializedName("title") val title: String,
    @SerializedName("lecturer") val lecturer: String,
    @SerializedName("classroom") val classroom: String,
    @SerializedName("type") val type: String,
    @SerializedName("subgroup") val subgroup: String,
    @SerializedName("time") val time: TimeResponse,
    @SerializedName("dates") val date: List<DateResponse>,
) {
    class TimeResponse(
        @SerializedName("start") val start: String,
        @SerializedName("end") val end: String,
    )

    class DateResponse(
        @SerializedName("frequency") val frequency: String,
        @SerializedName("date") val date: String,
    )
}