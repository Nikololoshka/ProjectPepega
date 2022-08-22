package com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RepositoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entities: List<RepositoryEntity>)

    @Query("SELECT * FROM repository_entries WHERE category = :category ORDER BY name")
    fun getAll(category: String): List<RepositoryEntity>

    @Query("DELETE FROM repository_entries WHERE category = :category")
    fun deleteAll(category: String)
}