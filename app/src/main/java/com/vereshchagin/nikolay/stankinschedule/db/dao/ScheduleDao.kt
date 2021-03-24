package com.vereshchagin.nikolay.stankinschedule.db.dao

import androidx.room.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.ScheduleResponse
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleWithPairs
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс для работы с БД расписаний.
 */
@Dao
interface ScheduleDao {

    /**
     * Возвращает flow списка всех расписаний.
     */
    @Query("SELECT * FROM schedules ORDER BY position ASC")
    fun getAllSchedules(): Flow<List<ScheduleItem>>

    /**
     * Возвращает flow списка всех пар расписания.
     */
    @Query("SELECT * FROM pairs WHERE schedule_id == :scheduleId")
    fun getAllPairs(scheduleId: Long): Flow<List<PairItem>>

    /**
     * Возвращает flow расписания с парами по названию.
     */
    @Transaction
    @Query("SELECT * FROM schedules WHERE schedule_name == :scheduleName LIMIT 1")
    fun getScheduleWithPairs(scheduleName: String): Flow<ScheduleWithPairs?>

    /**
     * Возвращает flow расписания с парами по ID.
     */
    @Transaction
    @Query("SELECT * FROM schedules WHERE id == :id LIMIT 1")
    fun getScheduleWithPairs(id: Long): Flow<ScheduleWithPairs?>

    /**
     * Возвращает flow элемента расписания.
     */
    @Query("SELECT * FROM schedules WHERE schedule_name == :scheduleName LIMIT 1")
    fun getScheduleItem(scheduleName: String): Flow<ScheduleItem?>

    /**
     * Возвращает количество расписаний в БД.
     */
    @Query("SELECT COUNT(*) FROM schedules")
    fun getScheduleCount(): Int

    /**
     * Проверяет, если расписания с данным названием в БД.
     */
    @Query("SELECT EXISTS(SELECT * FROM schedules WHERE schedule_name = :scheduleName)")
    suspend fun isScheduleExist(scheduleName: String): Boolean

    /**
     * Возвращает flow пары расписания.
     */
    @Query("SELECT * FROM pairs WHERE id == :id LIMIT 1")
    fun getPairItem(id: Long): Flow<PairItem?>

    /**
     * Добавляет ScheduleItem в БД.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertScheduleItem(schedule: ScheduleItem): Long

    /**
     * Обновляет ScheduleItem в БД.
     */
    @Update
    suspend fun updateScheduleItem(schedule: ScheduleItem)

    /**
     * Обновляет список с ScheduleItem в БД.
     */
    @Update
    suspend fun updateScheduleItems(schedules: List<ScheduleItem>)

    /**
     * Добавляет список пар расписания в БД.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPairs(pairs: List<PairItem>)

    /**
     * Обновляет пару в расписании. Если ее не было раньше (новая пара), то добавляет ее.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPairItem(pair: PairItem)

    /**
     * Удаляет пару расписания по PairItem.
     */
    @Delete
    suspend fun deletePairItem(pair: PairItem)

    /**
     * Удаляет расписание из БД по названию.
     */
    @Transaction
    @Query("DELETE FROM schedules WHERE schedule_name == :scheduleName")
    suspend fun deleteSchedule(scheduleName: String)

    /**
     * Удаляет расписание из БД по ScheduleItem.
     */
    @Transaction
    @Delete
    suspend fun deleteSchedule(schedule: ScheduleItem)

    /**
     * Добавляет расписание из ответа от сервера в БД.
     */
    @Transaction
    suspend fun insertScheduleResponse(scheduleName: String, response: ScheduleResponse) {
        val lastPosition = getScheduleCount()
        val id = insertScheduleItem(
            ScheduleItem(scheduleName).apply {
                position = lastPosition
            }
        )
        insertPairs(response.pairs.map { it.toPairItem(id) })
    }
}