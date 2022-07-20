package com.vereshchagin.nikolay.stankinschedule.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleCategoryEntry
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleItemEntry
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleUpdateEntry
import kotlinx.coroutines.flow.Flow
import org.joda.time.DateTime

/**
 * Интерфейс для работы с БД удаленного расписания.
 */
@Dao
interface RepositoryDao {

    /**
     * Возвращает источник категорий для пагинации.
     */
    @Query("SELECT * FROM repository_category_entries WHERE parent == :parent or (parent is null and :parent is null)")
    fun categoriesSource(parent: Int?): PagingSource<Int, ScheduleCategoryEntry>

    /**
     * Возвращает источник расписаний для пагинации.
     */
    @Query("SELECT * FROM repository_schedule_item_entries WHERE category == :category")
    fun schedulesSource(category: Int): PagingSource<Int, ScheduleItemEntry>

    @Query("SELECT * FROM repository_category_entries WHERE id == :id LIMIT 1")
    fun getScheduleCategory(id: Int): Flow<ScheduleCategoryEntry?>

    /**
     * Возвращает flow объект расписания репозитория.
     */
    @Query("SELECT * FROM repository_schedule_item_entries WHERE id == :id LIMIT 1")
    fun getScheduleEntry(id: Int): Flow<ScheduleItemEntry?>

    /**
     * Возвращает все обновления расписания.
     */
    @Query("SELECT * FROM repository_schedule_update_entries WHERE item == :id")
    fun getScheduleUpdates(id: Int): List<ScheduleUpdateEntry>

    /**
     * Возвращает, содержит ли категория расписания.
     */
    @Query("SELECT isNode FROM repository_category_entries WHERE id = :category")
    suspend fun isScheduleCategory(category: Int): Boolean

    /**
     * Возвращает, является ли репозиторий пустым.
     */
    @Query("SELECT NOT EXISTS(SELECT * FROM repository_schedule_item_entries)")
    suspend fun isRepositoryEmpty(): Boolean

    /**
     * Добавляет список категории удаленного расписаний в БД.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCategoryEntries(entries: List<ScheduleCategoryEntry>)

    /**
     * Добавляет список расписаний удаленного репозитория в БД.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleEntries(entries: List<ScheduleItemEntry>)

    /**
     * Добавляет список обновлений (версий) расписания в БД.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUpdateEntries(entries: List<ScheduleUpdateEntry>)

    /**
     * Обновляет расписание из удаленного репозитория в БД.
     */
    @Update
    suspend fun updateScheduleEntry(entry: ScheduleItemEntry)

    /**
     * Обновляет категорию расписание удаленного репозитория в БД.
     */
    @Update
    suspend fun updateScheduleCategoryEntry(entry: ScheduleCategoryEntry)

    /**
     * Удаляет все категории удаленного репозитория из БД
     */
    @Query("DELETE FROM repository_category_entries")
    suspend fun clearCategoryEntries()

    /**
     * Удаляет все расписания удаленного репозитория из БД.
     */
    @Query("DELETE FROM repository_schedule_item_entries")
    suspend fun clearScheduleEntries()

    /**
     * Удаляет все обновления (версии) расписания из БД.
     */
    @Query("DELETE FROM repository_schedule_update_entries WHERE item == :id")
    suspend fun clearScheduleUpdatesEntries(id: Int)

    /**
     * Обновляет версии для расписания в БД.
     */
    @Transaction
    suspend fun updateScheduleUpdateEntries(
        entry: ScheduleItemEntry,
        entries: List<ScheduleUpdateEntry>,
    ) {
        clearScheduleUpdatesEntries(entry.id)
        insertUpdateEntries(entries)
        updateScheduleEntry(entry.apply { date = DateTime.now() })
    }

    @Transaction
    suspend fun updateScheduleCategotyEntries(entries: List<ScheduleCategoryEntry>) {
        clearCategoryEntries()
        insertCategoryEntries(entries)
    }
}