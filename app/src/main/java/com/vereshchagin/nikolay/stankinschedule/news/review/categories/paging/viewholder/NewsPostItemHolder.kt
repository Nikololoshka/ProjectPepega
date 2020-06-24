package com.vereshchagin.nikolay.stankinschedule.news.review.categories.paging.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemNewsPostBinding
import com.vereshchagin.nikolay.stankinschedule.news.review.categories.paging.NewsPostAdapter
import com.vereshchagin.nikolay.stankinschedule.news.review.categories.repository.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.utils.formatDate
import com.vereshchagin.nikolay.stankinschedule.utils.parseDate

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
     * @param post данные о новости.
     */
    fun bind(post: NewsPost?) {
        val shimmerDrawable = ShimmerDrawable().apply {
            setShimmer(Shimmer.AlphaHighlightBuilder()
                .setDuration(1000)
                .setBaseAlpha(0.7f)
                .setHighlightAlpha(0.6f)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(true)
                .build())
        }

       glide.load(post?.logoUrl())
            .placeholder(shimmerDrawable)
            .transition(withCrossFade())
            .into(binding.newsPreview)

        post?.let {
            binding.newsTitle.text = post.title
            binding.newsDate.text = formatDate(parseDate(post.date))
            newsId = post.id
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            clickListener: NewsPostAdapter.OnNewsClickListener,
            glide: RequestManager
        ): NewsPostItemHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news_post, parent, false)
            return NewsPostItemHolder(clickListener, glide, view)
        }
    }
}