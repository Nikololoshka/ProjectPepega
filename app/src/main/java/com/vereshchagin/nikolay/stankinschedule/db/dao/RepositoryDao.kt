package com.vereshchagin.nikolay.stankinschedule.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.CategoryEntry
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.RepositoryResponse
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.ScheduleEntry
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс для работы с БД удаленного расписания.
 */
@Dao
interface RepositoryDao {

    /**
     * Возвращает источник категорий для пагинации.
     */
    @Query("SELECT * FROM repository_category_entries WHERE parent == :parent or (parent is null and :parent is null)")
    fun categoriesSource(parent: Int?): PagingSource<Int, CategoryEntry>

    /**
     * Возвращает источник расписаний для пагинации.
     */
    @Query("SELECT * FROM repository_schedule_entries WHERE category == :category")
    fun schedulesSource(category: Int): PagingSource<Int, ScheduleEntry>

    /**
     * Возвращает flow объект расписания репозитория.
     */
    @Query("SELECT * FROM repository_schedule_entries WHERE id == :id LIMIT 1")
    fun getScheduleEntry(id: Int): Flow<ScheduleEntry?>

    /**
     * Возвращает, содержит ли категория расписания.
     */
    @Query("SELECT EXISTS(SELECT * FROM repository_schedule_entries WHERE category = :category)")
    suspend fun isScheduleCategory(category: Int?): Boolean

    /**
     * Возвращает, является ли репозиторий пустым.
     */
    @Query("SELECT NOT EXISTS(SELECT * FROM repository_schedule_entries)")
    suspend fun isRepositoryEmpty(): Boolean

    /**
     * Добавляет список категории удаленного расписаний в БД.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCategoryEntries(entries: List<CategoryEntry>)

    /**
     * Добавляет список расписаний удаленного репозитория в БД.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertScheduleEntries(entries: List<ScheduleEntry>)

    /**
     * Удаляет все категории удаленного репозитория из БД
     */
    @Query("DELETE FROM repository_category_entries")
    suspend fun clearCategoryEntries()

    /**
     * Удаляет все расписания удаленного репозитория из БД.
     */
    @Query("DELETE FROM repository_schedule_entries")
    suspend fun clearScheduleEntries()

    /**
     * Добавляет списки категорий и расписаний удаленного репозитория в БД.
     */
    @Transaction
    suspend fun insertRepositoryResponse(response: RepositoryResponse) {
        clearCategoryEntries()
        clearScheduleEntries()

        insertCategoryEntries(response.categories)
        insertScheduleEntries(response.schedules)
    }
}