package com.vereshchagin.nikolay.stankinschedule.ui.home.news

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
    private val glide: RequestManager,
    private val count: Int = 3
) : RecyclerView.Adapter<NewsPostItemHolder>() {

    /**
     * Данные адаптера.
     */
    private var data: List<NewsItem>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsPostItemHolder {
        return NewsPostItemHolder.create(parent, clickListener, glide)
    }

    override fun onBindViewHolder(holder: NewsPostItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return count
    }

    /**
     * Устанавливает данные в адаптер.
     */
    fun submitList(newData: List<NewsItem>){
        data = newData
        notifyDataSetChanged()
    }

    /**
     * Возвращает элемент по позиции.
     */
    private fun getItem(position: Int): NewsItem? {
        val isEmpty = data.let { it == null || it.size <= position }
        return if (isEmpty) null else data?.get(position)
    }
}