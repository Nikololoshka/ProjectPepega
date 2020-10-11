package com.vereshchagin.nikolay.stankinschedule.ui.home

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import com.bumptech.glide.RequestManager
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsItem
import com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging.NewsPostAdapter
import com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging.viewholder.NewsPostItemHolder

/**
 * Адаптер для последних новостей на главной странице.
 *
 * @see NewsPostAdapter
 */
class NewsPostLatestAdapter(
    private val clickListener: NewsPostAdapter.OnNewsClickListener,
    private val glide: RequestManager
) : PagedListAdapter<NewsItem, NewsPostItemHolder>(NewsPostAdapter.POST_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsPostItemHolder {
        return NewsPostItemHolder.create(parent, clickListener, glide)
    }

    override fun onBindViewHolder(holder: NewsPostItemHolder, position: Int) {
        holder.bind(getItem(position))
    }
}