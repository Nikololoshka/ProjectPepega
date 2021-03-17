package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryItem
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging.holder.RepositoryItemViewHolder

/**
 *
 */
class RepositoryItemAdapter<T : RepositoryItem>(
    private val clickListener: (item: T) -> Unit,
) : PagingDataAdapter<T, RepositoryItemViewHolder<T>>(repositoryItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryItemViewHolder<T> {
        return RepositoryItemViewHolder.create(parent, clickListener)
    }

    override fun onBindViewHolder(holder: RepositoryItemViewHolder<T>, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        /**
         *
         */
        private fun <T : RepositoryItem> repositoryItemComparator() =
            object : DiffUtil.ItemCallback<T>() {

                override fun areItemsTheSame(oldItem: T, newItem: T) =
                    oldItem.data() == newItem.data()

                override fun areContentsTheSame(oldItem: T, newItem: T) =
                    oldItem.data() == newItem.data()
            }
    }
}