package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging

import androidx.paging.PagingSource
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryCategoryItem
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleServerRepository
import com.vereshchagin.nikolay.stankinschedule.utils.State

/**
 * Источник данных для категорий расписаний.
 */
class RepositoryCategorySource(
    private val repository: ScheduleServerRepository,
    private val categories: List<String>,
    private val useCache: Boolean
) : PagingSource<String, RepositoryCategoryItem>() {

    override suspend fun load(
        params: LoadParams<String>
    ): LoadResult<String, RepositoryCategoryItem> {
        try {
            when (val state = repository.category(params.key, useCache)) {
                // успешно
                is State.Success -> {
                    val current = categories.indexOf(params.key)

                    return LoadResult.Page(
                        listOf(state.data),
                        null,
                        if (current < categories.size - 1 ) categories[current + 1] else null
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