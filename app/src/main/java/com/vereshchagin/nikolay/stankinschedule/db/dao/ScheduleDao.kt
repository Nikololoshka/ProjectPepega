package com.vereshchagin.nikolay.stankinschedule.db.dao

import androidx.room.*
import com.vereshchagin.nikolay.stankinschedule.model.schedule.ScheduleResponse
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleWithPairs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.joda.time.DateTime

/**
 * Интерфейс для работы с БД расписаний.
 */
@Dao
interface ScheduleDao {

    /**
     * Возвращает flow списка всех расписаний.
     */
    @Query("SELECT * FROM schedule_items ORDER BY position ASC")
    fun getAllSchedules(): Flow<List<ScheduleItem>>

    /**
     * Возвращает flow списка всех пар расписания.
     */
    @Query("SELECT * FROM schedule_pairs WHERE schedule_id = :scheduleId")
    fun getAllPairs(scheduleId: Long): Flow<List<PairItem>>

    /**
     * Возвращает flow расписания с парами по названию.
     */
    @Transaction
    @Query("SELECT * FROM schedule_items WHERE schedule_name = :scheduleName LIMIT 1")
    fun getScheduleWithPairs(scheduleName: String): Flow<ScheduleWithPairs?>

    /**
     * Возвращает flow расписания с парами по ID.
     */
    @Transaction
    @Query("SELECT * FROM schedule_items WHERE id == :id LIMIT 1")
    fun getScheduleWithPairs(id: Long): Flow<ScheduleWithPairs?>

    /**
     * Возвращает flow элемента расписания.
     */
    @Query("SELECT * FROM schedule_items WHERE schedule_name = :scheduleName LIMIT 1")
    fun getScheduleItem(scheduleName: String): Flow<ScheduleItem?>

    /**
     * Возвращает количество расписаний в БД.
     */
    @Query("SELECT COUNT(*) FROM schedule_items")
    fun getScheduleCount(): Int

    /**
     * Возвращает flow списка с синхронизованными расписаниями.
     */
    @Query("SELECT * FROM schedule_items WHERE synced = :synced")
    fun getScheduleSyncList(synced: Boolean = true): Flow<List<ScheduleItem>>

    /**
     * Проверяет, если расписания с данным названием в БД.
     */
    @Query("SELECT EXISTS(SELECT * FROM schedule_items WHERE schedule_name = :scheduleName)")
    suspend fun isScheduleExist(scheduleName: String): Boolean

    /**
     * Возвращает flow пары расписания.
     */
    @Query("SELECT * FROM schedule_pairs WHERE id == :id LIMIT 1")
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
    suspend fun updateScheduleItems(schedule_items: List<ScheduleItem>)

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
     * Удаляет все пары расписания по ID.
     */
    @Query("DELETE FROM schedule_pairs WHERE schedule_id = :scheduleId")
    suspend fun deleteSchedulePairs(scheduleId: Long)

    /**
     * Удаляет расписание из БД по названию.
     */
    @Transaction
    @Query("DELETE FROM schedule_items WHERE schedule_name = :scheduleName")
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
    suspend fun insertScheduleResponse(
        scheduleName: String,
        response: ScheduleResponse,
        replaceExist: Boolean = false,
        synced: Boolean = false,
    ) {
        if (replaceExist) {
            val item = getScheduleItem(scheduleName).first()
            if (item != null) {
                // обновляем информацию
                item.synced = synced
                if (synced) item.lastUpdate = DateTime.now()

                updateScheduleItem(item)

                // обновляем пары
                deleteSchedulePairs(item.id)
                insertPairs(response.pairs.map { it.toPairItem(item.id) })
                return
            }
        }

        val lastPosition = getScheduleCount()
        val id = insertScheduleItem(
            ScheduleItem(scheduleName).apply {
                this.position = lastPosition
                this.synced = synced
                this.lastUpdate = if (synced) DateTime.now() else null
            }
        )
        insertPairs(response.pairs.map { it.toPairItem(id) })
    }
}