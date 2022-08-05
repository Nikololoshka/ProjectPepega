package com.vereshchagin.nikolay.stankinschedule.schedule.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс для работы с БД расписаний.
 */
@Dao
interface ScheduleDao {

    /**
     * Возвращает flow списка всех расписаний.
     */
    @Query("SELECT * FROM schedule_entities ORDER BY position ASC")
    fun getAllSchedules(): Flow<List<ScheduleEntity>>

    /**
     * Возвращает flow списка всех пар расписания.
     */
    @Query("SELECT * FROM schedule_pair_entities WHERE schedule_id = :scheduleId")
    fun getAllPairs(scheduleId: Long): Flow<List<PairEntity>>

    /**
     * Возвращает flow расписания с парами по названию.
     */
    @Transaction
    @Query("SELECT * FROM schedule_entities WHERE schedule_name = :scheduleName LIMIT 1")
    fun getScheduleWithPairs(scheduleName: String): Flow<ScheduleWithPairs?>

    /**
     * Возвращает flow расписания с парами по ID.
     */
    @Transaction
    @Query("SELECT * FROM schedule_entities WHERE id == :id LIMIT 1")
    fun getScheduleWithPairs(id: Long): Flow<ScheduleWithPairs?>

    /**
     * Возвращает flow элемента расписания.
     */
    @Query("SELECT * FROM schedule_entities WHERE schedule_name = :scheduleName LIMIT 1")
    fun getScheduleEntiry(scheduleName: String): Flow<ScheduleEntity?>

    /**
     * Возвращает flow элемента расписания.
     */
    @Query("SELECT * FROM schedule_entities WHERE id = :id LIMIT 1")
    fun getScheduleEntiry(id: Long): Flow<ScheduleEntity?>

    /**
     * Возвращает количество расписаний в БД.
     */
    @Query("SELECT COUNT(*) FROM schedule_entities")
    suspend fun getScheduleCount(): Int

    /**
     * Возвращает flow списка с синхронизованными расписаниями.
     */
    @Query("SELECT * FROM schedule_entities WHERE synced = :synced")
    fun getScheduleSyncList(synced: Boolean = true): Flow<List<ScheduleEntity>>

    /**
     * Проверяет, если расписания с данным названием в БД.
     */
    @Query("SELECT EXISTS(SELECT * FROM schedule_entities WHERE schedule_name = :scheduleName)")
    suspend fun isScheduleExist(scheduleName: String): Boolean

    /**
     * Возвращает flow пары расписания.
     */
    @Query("SELECT * FROM schedule_pair_entities WHERE id == :id LIMIT 1")
    fun getPairEntity(id: Long): Flow<PairEntity?>

    /**
     * Добавляет ScheduleItem в БД.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertScheduleEntity(schedule: ScheduleEntity): Long

    /**
     * Обновляет ScheduleItem в БД.
     */
    @Update
    suspend fun updateScheduleItem(schedule: ScheduleEntity)

    /**
     * Обновляет список с ScheduleItem в БД.
     */
    @Update
    suspend fun updateScheduleItems(schedule_entities: List<ScheduleEntity>)

    /**
     * Добавляет список пар расписания в БД.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPairs(pairs: List<PairEntity>)

    /**
     * Обновляет пару в расписании. Если ее не было раньше (новая пара), то добавляет ее.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPairEntity(pair: PairEntity)

    /**
     * Удаляет пару расписания по PairItem.
     */
    @Delete
    suspend fun deletePairEntity(pair: PairEntity)

    /**
     * Удаляет все пары расписания по ID.
     */
    @Query("DELETE FROM schedule_pair_entities WHERE schedule_id = :scheduleId")
    suspend fun deleteSchedulePairs(scheduleId: Long)

    /**
     * Удаляет расписание из БД по названию.
     */
    @Transaction
    @Query("DELETE FROM schedule_entities WHERE schedule_name = :scheduleName")
    suspend fun deleteSchedule(scheduleName: String)

    /**
     * Удаляет расписание из БД по названию.
     */
    @Transaction
    @Query("DELETE FROM schedule_entities WHERE id = :scheduleId")
    suspend fun deleteSchedule(scheduleId: Long)

    /**
     * Удаляет расписание из БД по ScheduleItem.
     */
    @Transaction
    @Delete
    suspend fun deleteSchedule(schedule: ScheduleEntity)
}