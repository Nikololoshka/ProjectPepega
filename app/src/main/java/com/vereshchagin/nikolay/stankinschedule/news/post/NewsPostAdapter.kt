package com.vereshchagin.nikolay.stankinschedule.news.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.news.model.NewsPost

/**
 * Адаптер для списка новостей.
 */
class NewsPostAdapter() : PagedListAdapter<NewsPost, NewsPostHolder>(POST_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsPostHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsPostHolder(view)
    }

    override fun onBindViewHolder(postHolder: NewsPostHolder, position: Int) {
        postHolder.bind(getItem(position))
    }

    companion object {
        /**
         * Компаратор для сравнения новостей.
         */
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<NewsPost>() {

            override fun areItemsTheSame(oldItem: NewsPost, newItem: NewsPost): Boolean =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: NewsPost, newItem: NewsPost): Boolean =
                    oldItem == newItem
        }
    }
}
