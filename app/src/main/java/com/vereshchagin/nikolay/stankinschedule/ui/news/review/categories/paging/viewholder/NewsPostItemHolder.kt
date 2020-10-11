package com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemNewsPostBinding
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsItem
import com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging.NewsPostAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.DateUtils.Companion.formatDate
import com.vereshchagin.nikolay.stankinschedule.utils.DateUtils.Companion.parseDate
import com.vereshchagin.nikolay.stankinschedule.utils.DrawableUtils

/**
 * Элемент новости в списке (т.е. сама новость).
 */
class NewsPostItemHolder(
    private val clickListener: NewsPostAdapter.OnNewsClickListener,
    private val glide: RequestManager,
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemNewsPostBinding.bind(itemView)
    private var newsId: Int? = null

    init {
        itemView.setOnClickListener {
            newsId?.let {
                clickListener.onNewsClick(it)
            }
        }
    }

    /**
     * Связывает данные с элементом.
     * @param item данные о новости.
     */
    fun bind(item: NewsItem?) {
        val shimmerDrawable = DrawableUtils.createShimmerDrawable()

        glide.load(item?.logoUrl())
            .placeholder(shimmerDrawable)
            .transition(withCrossFade())
            .into(binding.newsPreview)

        item?.let {
            binding.newsTitle.text = item.title
            binding.newsDate.text = parseDate(item.onlyDate())?.let { formatDate(it) }
            newsId = item.id
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            clickListener: NewsPostAdapter.OnNewsClickListener,
            glide: RequestManager
        ): NewsPostItemHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_news_post, parent, false)
            return NewsPostItemHolder(clickListener, glide, view)
        }
    }
}