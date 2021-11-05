package com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.response

import com.google.gson.annotations.SerializedName


class PairResponse(
    @SerializedName("title")
    val title: String,
    @SerializedName("lecturer")
    val lecturer: String,
    @SerializedName("classroom")
    val classroom: String,
    @SerializedName("type")
    var type: String,
    @SerializedName("subgroup")
    var subgroup: String,
    @SerializedName("time")
    var time: TimeResponse,
    @SerializedName("dates")
    var dates: List<DateResponse>,
) {
    class TimeResponse(
        @SerializedName("start")
        val start: String,
        @SerializedName("end")
        val end: String,
    )

    class DateResponse(
        @SerializedName("date")
        val date: String,
        @SerializedName("frequency")
        val frequency: String,
    )
}