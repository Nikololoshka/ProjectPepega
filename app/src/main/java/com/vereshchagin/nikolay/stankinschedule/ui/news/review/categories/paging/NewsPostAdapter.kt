package com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.RequestManager
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsItem
import com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging.viewholder.NewsPostItemHolder

/**
 * Адаптер для списка новостей.
 */
class NewsPostAdapter(
    private val clickListener: (newsId: Int) -> Unit,
    private val glide: RequestManager,
) : PagingDataAdapter<NewsItem, NewsPostItemHolder>(POST_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsPostItemHolder {
        return NewsPostItemHolder.create(parent, clickListener, glide)
    }

    override fun onBindViewHolder(holder: NewsPostItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        /**
         * Компаратор для сравнения новостей.
         */
        @JvmStatic
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<NewsItem>() {

            override fun areItemsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean =
                oldItem == newItem
        }
    }
}