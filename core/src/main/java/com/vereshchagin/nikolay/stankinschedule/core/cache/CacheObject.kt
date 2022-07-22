package com.vereshchagin.nikolay.stankinschedule.core.cache

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

class CacheObject(
    @SerializedName("data") val data: JsonElement?,
    @SerializedName("time") val time: DateTime?,
)