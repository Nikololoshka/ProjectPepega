package com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryItem


@Entity(tableName = "category_entries")
class CategoryEntry(
    @PrimaryKey
    @SerializedName("id")
    val id: Int,
    @SerializedName("parent")
    val parent: Int?,
    @SerializedName("name")
    val name: String,
) : RepositoryItem {

    override fun data() = name

}