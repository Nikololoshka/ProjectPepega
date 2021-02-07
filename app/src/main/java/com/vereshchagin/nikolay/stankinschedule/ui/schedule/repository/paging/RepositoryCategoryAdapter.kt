package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryCategoryItem

/**
 * Адаптер для ViewPager2 с категориями расписаний.
 */
class RepositoryCategoryAdapter :
    PagingDataAdapter<RepositoryCategoryItem, RepositoryCategoryHolder>(CATEGORY_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryCategoryHolder {
        return RepositoryCategoryHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RepositoryCategoryHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        /**
         * Компаратор для сравнения категорий расписаний.
         */
        @JvmStatic
        val CATEGORY_COMPARATOR = object : DiffUtil.ItemCallback<RepositoryCategoryItem>() {
            override fun areItemsTheSame(
                oldItem: RepositoryCategoryItem,
                newItem: RepositoryCategoryItem
            ): Boolean =
                oldItem.categoryName == newItem.categoryName

            override fun areContentsTheSame(
                oldItem: RepositoryCategoryItem,
                newItem: RepositoryCategoryItem
            ): Boolean =
                oldItem == newItem
        }
    }
}