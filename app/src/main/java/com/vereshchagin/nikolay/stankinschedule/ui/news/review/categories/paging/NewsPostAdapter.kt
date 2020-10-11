package com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.api.NetworkState
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsItem
import com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging.viewholder.NetworkStateItemHolder
import com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging.viewholder.NewsPostItemHolder

/**
 * Адаптер для списка новостей.
 */
class NewsPostAdapter(
    private val clickListener: OnNewsClickListener,
    private val glide: RequestManager,
    private val retryCallback: () -> Unit
) : PagedListAdapter<NewsItem, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    /**
     * Callback для нажатия по новости.
     */
    interface OnNewsClickListener {
        /**
         * Вызывается, когда нажата новость.
         * @param newsId ID новости.
         */
        fun onNewsClick(newsId: Int)
    }

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_news_post -> NewsPostItemHolder.create(parent, clickListener, glide)
            R.layout.item_network_state -> NetworkStateItemHolder.create(parent, retryCallback)
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_news_post -> (holder as NewsPostItemHolder).bind(getItem(position))
            R.layout.item_network_state -> (holder as NetworkStateItemHolder).bind(networkState, position)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int):  Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.item_network_state
        } else {
            R.layout.item_news_post
        }
    }

    /**
     * Проверяет, если в списке дополнительный элемент.
     */
    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = networkState
        val hadExtraRow = hasExtraRow()

        networkState = newNetworkState
        val hasExtraRow = hasExtraRow()

        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
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
