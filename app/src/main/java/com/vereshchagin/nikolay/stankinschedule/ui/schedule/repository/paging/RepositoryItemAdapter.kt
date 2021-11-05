package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleRepositoryItem
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging.viewholder.RepositoryItemViewHolder

/**
 * Адаптер для элементов репозитория.
 */
class RepositoryItemAdapter<T : ScheduleRepositoryItem>(
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
         * Компаратор для сравнения элементов репозитория.
         */
        private fun <T : ScheduleRepositoryItem> repositoryItemComparator() =
            object : DiffUtil.ItemCallback<T>() {

                override fun areItemsTheSame(oldItem: T, newItem: T) =
                    oldItem.data() == newItem.data()

                override fun areContentsTheSame(oldItem: T, newItem: T) =
                    oldItem.data() == newItem.data()
            }
    }
}