package com.vereshchagin.nikolay.stankinschedule.db.dao

import android.util.Log
import androidx.room.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.CategoryEntry
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.RepositoryResponse
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.ScheduleEntry

@Dao
interface RepositoryDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCategoryEntries(entries: List<CategoryEntry>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertScheduleEntries(entries: List<ScheduleEntry>)


    @Query("DELETE FROM category_entries")
    suspend fun clearCategoryEntries()

    @Query("DELETE FROM schedule_entries")
    suspend fun clearScheduleEntries()

    @Transaction
    suspend fun insertRepositoryResponse(response: RepositoryResponse) {
        clearCategoryEntries()
        clearScheduleEntries()
        Log.d("MyLog", "insertRepositoryResponse: ${response.schedules.size}")
        insertCategoryEntries(response.categories)
        insertScheduleEntries(response.schedules)
    }
}