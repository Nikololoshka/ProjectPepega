package com.vereshchagin.nikolay.stankinschedule.core.data.mapper

import androidx.paging.PagingSource
import androidx.paging.PagingState

class PagingSourceMapper<K : Any, V : Any, R : Any>(
    private val oldSource: PagingSource<K, V>,
    private val mapper: (oldItem: V) -> R
) : PagingSource<K, R>() {

    override val jumpingSupported: Boolean get() = oldSource.jumpingSupported

    override fun getRefreshKey(state: PagingState<K, R>): K? {
        return oldSource.getRefreshKey(
            PagingState(
                pages = emptyList(),
                leadingPlaceholderCount = 0,
                anchorPosition = state.anchorPosition,
                config = state.config,
            )
        )
    }

    override suspend fun load(params: LoadParams<K>): LoadResult<K, R> {
        return when (val originalResult = oldSource.load(params)) {
            is LoadResult.Error -> LoadResult.Error(originalResult.throwable)
            is LoadResult.Invalid -> LoadResult.Invalid()
            is LoadResult.Page -> LoadResult.Page(
                data = originalResult.data.map(mapper),
                prevKey = originalResult.prevKey,
                nextKey = originalResult.nextKey,
            )
        }
    }
}

fun <K : Any, V : Any, R : Any> PagingSource<K, V>.transform(
    mapper: (oldItem: V) -> R
): PagingSource<K, R> {
    return PagingSourceMapper(
        oldSource = this,
        mapper = mapper
    )
}