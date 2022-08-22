package com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repository_entries")
data class RepositoryEntity(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "category") val category: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}