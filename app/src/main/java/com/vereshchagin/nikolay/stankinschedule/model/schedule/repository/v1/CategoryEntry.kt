package com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Объект категории в удаленном репозитории.
 */
@Entity(tableName = "repository_category_entries")
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