package com.vereshchagin.nikolay.stankinschedule.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.CategoryEntry
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.RepositoryResponse
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.ScheduleEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface RepositoryDao {

    @Query("SELECT * FROM category_entries WHERE parent == :parent or (parent is null and :parent is null)")
    fun categoriesSource(parent: Int?): PagingSource<Int, CategoryEntry>

    @Query("SELECT * FROM schedule_entries WHERE category == :category")
    fun schedulesSource(category: Int): PagingSource<Int, ScheduleEntry>

    @Query("SELECT * FROM schedule_entries WHERE id == :id LIMIT 1")
    fun getScheduleEntry(id: Int): Flow<ScheduleEntry?>

    @Query("SELECT EXISTS(SELECT * FROM schedule_entries WHERE category = :category)")
    suspend fun isScheduleCategory(category: Int?): Boolean

    @Query("SELECT NOT EXISTS(SELECT * FROM schedule_entries)")
    suspend fun isRepositoryEmpty(): Boolean

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

        insertCategoryEntries(response.categories)
        insertScheduleEntries(response.schedules)
    }
}