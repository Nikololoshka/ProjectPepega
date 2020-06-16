package com.vereshchagin.nikolay.stankinschedule.news.post

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemNewsBinding
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemNewsPostBinding
import com.vereshchagin.nikolay.stankinschedule.news.model.NewsPost
import org.jetbrains.annotations.NotNull

/**
 * Элемент в списке новостей (т.е. сама новость).
 */
class NewsPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding: ItemNewsPostBinding = ItemNewsPostBinding.bind(itemView)

    /**
     * Связывает данные с элементом.
     * @param post данные о новости.
     */
    fun bind(post: NewsPost?) {
        binding.newsTitle.text = post?.title
    }
}