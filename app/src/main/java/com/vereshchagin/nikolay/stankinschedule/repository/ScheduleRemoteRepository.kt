package com.vereshchagin.nikolay.stankinschedule.repository

import androidx.paging.PagingSource
import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.api.ScheduleRemoteRepositoryAPI
import com.vereshchagin.nikolay.stankinschedule.db.dao.RepositoryDao
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleCategoryEntry
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleRepositoryInfo
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleRepositoryItem
import com.vereshchagin.nikolay.stankinschedule.utils.CacheFolder
import com.vereshchagin.nikolay.stankinschedule.utils.State
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.gson.DateTimeTypeConverter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.joda.time.DateTime
import retrofit2.await
import javax.inject.Inject

/**
 * Репозиторий по работе с удаленном репозиторием расписаний.
 * @param cacheFolder папка с кэшом.
 * @param api API удаленного репозитория расписаний.
 * @param dao интерфейс для кэширования данных в БД.
 */
class ScheduleRemoteRepository @Inject constructor(
    private val cacheFolder: CacheFolder,
    private val api: ScheduleRemoteRepositoryAPI,
    private val dao: RepositoryDao,
) {

    init {
        cacheFolder.addStartedPath(REPOSITORY_FOLDER)
        cacheFolder.gson = GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, DateTimeTypeConverter())
            .create()
    }

    /**
     * Возвращает flow обновлений (версий) для расписания.
     */
    fun scheduleUpdates(scheduleId: Int) = flow {
        val entry = dao.getScheduleEntry(scheduleId).first()

        // есть ли такое расписание
        if (entry == null) {
            emit(State.failed(NullPointerException("Entry $scheduleId is null")))
            return@flow
        }

        // действителен ли кэш
        val updates = if (entry.isValid()) {
            dao.getScheduleUpdates(entry.id)
        } else {
            val newUpdates = api.updates(scheduleId).await()
            dao.updateScheduleUpdateEntries(entry, newUpdates)
            newUpdates
        }
        emit(State.success(updates))
    }

    suspend fun schedules(parentCategory: Int) {
        val category = dao.getScheduleCategory(parentCategory).first()
        if (category == null || !category.isValid()) {
            val schedules = api.schedules(parentCategory).await()
            dao.insertScheduleEntries(schedules)
        }
    }

    fun rootCategorySource() = dao.categoriesSource(null)

    suspend fun isScheduleCategory(parentCategory: Int) = dao.isScheduleCategory(parentCategory)

    @Suppress("UNCHECKED_CAST")
    fun categorySource(
        isNode: Boolean,
        parentCategory: Int,
    ): PagingSource<Int, ScheduleRepositoryItem> {
        return if (isNode) {
            dao.schedulesSource(parentCategory)
        } else {
            dao.categoriesSource(parentCategory)
        } as PagingSource<Int, ScheduleRepositoryItem>
    }

    fun refreshCategory(parentCategory: Int) = flow {
        emit(State.loading())
        val parentCategoryEntry = dao.getScheduleCategory(parentCategory).first()
        if (parentCategoryEntry == null) {
            emit(State.failed(
                NullPointerException("ScheduleRepositoryItem(id=$parentCategory) is null")
            ))
            return@flow
        }

        if (parentCategoryEntry.isValid()) {
            emit(State.success(parentCategoryEntry))

        } else {
            val updatedCategoryEntry = if (parentCategoryEntry.isNode) {
                refreshScheduleItemEntry(parentCategoryEntry)
            } else {
                refreshScheduleCategoryEntry(parentCategoryEntry)
            }
            emit(State.success(updatedCategoryEntry))
        }
    }

    /**
     * Обновляет список категорий для родительской категории.
     */
    private suspend fun refreshScheduleCategoryEntry(
        parentCategoryEntry: ScheduleCategoryEntry,
    ): ScheduleCategoryEntry {
        val categoryEntries = api.categories(parentCategoryEntry.id).await()
        dao.insertCategoryEntries(categoryEntries)

        val updatedCategoryEntry = ScheduleCategoryEntry(parentCategoryEntry, DateTime.now())
        dao.updateScheduleCategoryEntry(updatedCategoryEntry)
        return updatedCategoryEntry
    }

    /**
     * Обновляет список расписаний для родительской категории.
     */
    private suspend fun refreshScheduleItemEntry(
        parentCategoryEntry: ScheduleCategoryEntry,
    ): ScheduleCategoryEntry {
        val scheduleEntries = api.schedules(parentCategoryEntry.id).await()
        dao.insertScheduleEntries(scheduleEntries)

        val updatedCategoryEntry = ScheduleCategoryEntry(parentCategoryEntry, DateTime.now())
        dao.updateScheduleCategoryEntry(updatedCategoryEntry)
        return updatedCategoryEntry
    }

    /**
     * Возвращает описание репозитория с расписаниями.
     */
    fun description(useCache: Boolean = true) = flow {
        emit(State.loading())

        // проверка кэша
        if (useCache) {
            val cache = cacheFolder.loadFromCache(
                ScheduleRepositoryInfo.Description::class.java, REPOSITORY_INFO
            )

            if (cache != null && cache.isValid()) {
                emit(State.success(cache))
                return@flow
            }
        }

        val info = api.info().await()
        info.description.time = DateTime.now()

        cacheFolder.saveToCache(info.description, REPOSITORY_INFO)
        dao.updateScheduleCategotyEntries(info.categories.map { item ->
            if (!item.isNode) {
                ScheduleCategoryEntry(item, DateTime.now())
            } else {
                item
            }
        })

        emit(State.success(info.description))
    }


    companion object {
        private const val TAG = "ScheduleRepositoryLog"

        private const val REPOSITORY_FOLDER = "schedule_remote_repository"
        private const val REPOSITORY_INFO = "info"
    }
}