package com.vereshchagin.nikolay.stankinschedule.core.cache

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

class CacheContainer<T : Any>(
    @SerializedName("data") val data: T,
    @SerializedName("time") val cacheTime: DateTime,
) {
    operator fun component1(): T = data
    operator fun component2(): DateTime = cacheTime

    override fun toString(): String {
        return "CacheContainer(data=$data, cacheTime=$cacheTime)"
    }
}