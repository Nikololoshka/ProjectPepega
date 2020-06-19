package com.vereshchagin.nikolay.stankinschedule.news.post.paging

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.news.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.news.network.NetworkState
import com.vereshchagin.nikolay.stankinschedule.news.post.paging.viewholder.NetworkStateItemHolder
import com.vereshchagin.nikolay.stankinschedule.news.post.paging.viewholder.NewsPostItemHolder

/**
 * Адаптер для списка новостей.
 */
class NewsPostAdapter(private val retryCallback: () -> Unit) : PagedListAdapter<NewsPost, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_news_post -> NewsPostItemHolder.create(parent)
            R.layout.item_network_state -> NetworkStateItemHolder.create(parent, retryCallback)
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_news_post -> (holder as NewsPostItemHolder).bind(getItem(position))
            R.layout.item_network_state -> (holder as NetworkStateItemHolder).bind(networkState)
        }
    }

    override fun getItemCount(): Int {
        println("Count " + super.getItemCount())
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
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<NewsPost>() {

            override fun areItemsTheSame(oldItem: NewsPost, newItem: NewsPost): Boolean =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: NewsPost, newItem: NewsPost): Boolean =
                    oldItem == newItem
        }
    }
}
