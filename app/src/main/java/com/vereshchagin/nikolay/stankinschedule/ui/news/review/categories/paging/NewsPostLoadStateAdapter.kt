package com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging.viewholder.NewsLoadStateItemHolder

/**
 * Адаптер для отображения текущего состояния при загрузке новостей.
 */
class NewsPostLoadStateAdapter(
    private val retryCallback: () -> Unit
) : LoadStateAdapter<NewsLoadStateItemHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): NewsLoadStateItemHolder {
        return NewsLoadStateItemHolder.create(parent, retryCallback)
    }

    override fun onBindViewHolder(holder: NewsLoadStateItemHolder, loadState: LoadState) {
        holder.bind(loadState)
    }
}