package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemRepositoryCategoryBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryCategoryItem

/**
 * Holder категории в репозитории.
 */
class RepositoryCategoryHolder(
    private val binding: ItemRepositoryCategoryBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val adapter = RepositoryScheduleItemAdapter()

    init {
        binding.repositoryCategory.adapter = adapter
    }

    /**
     * Устанавливает данные в Holder.
     */
    fun bind(item: RepositoryCategoryItem?) {
        if (item != null) {
            adapter.submitList(item)
        }

        binding.showContent = item != null
    }

    companion object {
        /**
         * Создает Holder для адаптера с категориями.
         */
        @JvmStatic
        fun create(parent: ViewGroup): RepositoryCategoryHolder {
            return RepositoryCategoryHolder(
                ItemRepositoryCategoryBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }
}