package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging

import androidx.paging.PagingSource
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryCategoryItem
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleServerRepository
import com.vereshchagin.nikolay.stankinschedule.utils.State
import java.io.File

/**
 * Источник данных для категорий расписаний.
 */
class RepositoryCategorySource(
    private val repository: ScheduleServerRepository,
    private val cacheFolder: File
) : PagingSource<String, RepositoryCategoryItem>() {

    override suspend fun load(
        params: LoadParams<String>
    ): LoadResult<String, RepositoryCategoryItem> {
        try {
            when (val state = repository.category(params.key, cacheFolder)) {
                // успешно
                is State.Success -> {
                    return LoadResult.Page(
                        listOf(state.data),
                        null,
                        null
                    )
                }
                // ошибка
                is State.Failed -> {
                    return LoadResult.Error(state.error)
                }
                // неизвестная ошибка
                else -> {
                    return LoadResult.Error(
                        IllegalStateException("Unknown loading state")
                    )
                }
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}