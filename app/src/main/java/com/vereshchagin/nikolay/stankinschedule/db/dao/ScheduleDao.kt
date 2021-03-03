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

    @Query("SELECT * FROM schedules ORDER BY position ASC")
    fun getAllSchedules(): Flow<List<ScheduleItem>>

    @Query("SELECT * FROM pairs WHERE schedule_id == :scheduleId")
    fun getAllPairs(scheduleId: Long): Flow<List<PairItem>>

    @Transaction
    @Query("SELECT * FROM schedules WHERE schedule_name == :scheduleName LIMIT 1")
    fun getScheduleWithPairs(scheduleName: String): Flow<ScheduleWithPairs?>

    @Transaction
    @Query("SELECT * FROM schedules WHERE id == :id LIMIT 1")
    fun getScheduleWithPairs(id: Long): Flow<ScheduleWithPairs?>

    @Query("SELECT * FROM schedules WHERE schedule_name == :scheduleName LIMIT 1")
    fun getScheduleItem(scheduleName: String): Flow<ScheduleItem?>

    @Query("SELECT COUNT(*) FROM schedules")
    fun getScheduleCount(): Int

    @Query("SELECT EXISTS(SELECT * FROM schedules WHERE schedule_name = :scheduleName)")
    suspend fun isScheduleExist(scheduleName: String): Boolean

    @Query("SELECT * FROM pairs WHERE id == :id LIMIT 1")
    fun getPairItem(id: Long): Flow<PairItem?>


    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertScheduleItem(schedule: ScheduleItem): Long

    @Update
    suspend fun updateScheduleItem(schedule: ScheduleItem)

    @Update
    suspend fun updateScheduleItems(schedules: List<ScheduleItem>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPairs(pairs: List<PairItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPairItem(pair: PairItem)

    @Delete
    suspend fun deletePairItem(pair: PairItem)

    @Transaction
    @Query("DELETE FROM schedules WHERE schedule_name == :scheduleName")
    suspend fun deleteSchedule(scheduleName: String)

    @Transaction
    @Delete
    suspend fun deleteSchedule(schedule: ScheduleItem)

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