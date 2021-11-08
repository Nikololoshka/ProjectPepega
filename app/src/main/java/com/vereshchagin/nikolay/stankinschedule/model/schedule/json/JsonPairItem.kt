package com.vereshchagin.nikolay.stankinschedule.model.schedule.json

import com.google.gson.annotations.SerializedName
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem


class JsonPairItem(
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
    var time: JsonTimeItem,
    @SerializedName("dates")
    var dates: List<JsonDateItem>,
) {

    constructor(
        pair: PairItem,
    ) : this(
        pair.title,
        pair.lecturer,
        pair.classroom,
        pair.type.tag,
        pair.subgroup.tag,
        pair.time.toJson(),
        pair.date.toJsonItems()
    )

    class JsonTimeItem(
        @SerializedName("start")
        val start: String,
        @SerializedName("end")
        val end: String,
    )

    class JsonDateItem(
        @SerializedName("date")
        val date: String,
        @SerializedName("frequency")
        val frequency: String,
    )
}