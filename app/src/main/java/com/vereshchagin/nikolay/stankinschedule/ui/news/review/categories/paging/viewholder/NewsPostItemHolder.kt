package com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemNewsPostBinding
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsItem
import com.vereshchagin.nikolay.stankinschedule.utils.DateUtils.Companion.formatDate
import com.vereshchagin.nikolay.stankinschedule.utils.DateUtils.Companion.parseDate
import com.vereshchagin.nikolay.stankinschedule.utils.DrawableUtils

/**
 * Элемент новости в списке (т.е. сама новость).
 */
class NewsPostItemHolder(
    private val binding: ItemNewsPostBinding,
    private val clickListener: (newsId: Int, newsTitle: String?) -> Unit,
    private val glide: RequestManager
) : RecyclerView.ViewHolder(binding.root) {

    private var newsId: Int? = null
    private var newsTitle: String? = null

    init {
        itemView.setOnClickListener {
            newsId?.let { clickListener(it, newsTitle) }
        }
    }

    /**
     * Связывает данные новости с элементом.
     */
    fun bind(item: NewsItem?) {
        val shimmerDrawable = DrawableUtils.createShimmerDrawable()

        glide.load(item?.logoUrl())
            .placeholder(shimmerDrawable)
            .transition(
                withCrossFade(
                    DrawableCrossFadeFactory.Builder()
                        .setCrossFadeEnabled(true)
                        .build()
                )
            )
            .into(binding.newsPreview)

        item?.let {
            binding.newsTitle.text = item.title
            binding.newsDate.text = parseDate(item.onlyDate())?.let { formatDate(it) }
            newsId = item.id
            newsTitle = item.title
        }
    }

    companion object {
        /**
         * Возвращает holder новости в списке,
         */
        fun create(
            parent: ViewGroup,
            clickListener: (newsId: Int, newsTitle: String?) -> Unit,
            glide: RequestManager
        ): NewsPostItemHolder {
            return NewsPostItemHolder(
                ItemNewsPostBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                clickListener,
                glide
            )
        }
    }
}