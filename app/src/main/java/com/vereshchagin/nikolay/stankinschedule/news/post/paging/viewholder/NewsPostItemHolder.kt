package com.vereshchagin.nikolay.stankinschedule.news.post.paging.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemNewsPostBinding
import com.vereshchagin.nikolay.stankinschedule.news.model.NewsPost

/**
 * Элемент новости в списке (т.е. сама новость).
 */
class NewsPostItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemNewsPostBinding.bind(itemView)

    /**
     * Связывает данные с элементом.
     * @param post данные о новости.
     */
    fun bind(post: NewsPost?) {
        binding.newsTitle.text = post?.title
    }

    companion object {
        fun create(parent: ViewGroup): NewsPostItemHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news_post, parent, false)
            return NewsPostItemHolder(view)
        }
    }
}