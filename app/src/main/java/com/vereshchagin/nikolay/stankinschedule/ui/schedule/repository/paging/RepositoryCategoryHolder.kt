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
    private val binding: ItemRepositoryCategoryBinding,
    private val clickListener: (scheduleName: String, categoryName: String, id: Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    private val adapter = RepositoryScheduleItemAdapter { scheduleName, position ->
        clickListener(scheduleName, categoryName, bindingAdapterPosition * 1000 + position)
    }

    private var categoryName: String = ""

    init {
        binding.repositoryCategory.adapter = adapter
    }

    /**
     * Устанавливает данные в Holder.
     */
    fun bind(item: RepositoryCategoryItem?) {
        if (item != null) {
            adapter.submitList(item)
            categoryName = item.categoryName
        }

        binding.showContent = item != null
    }

    companion object {
        /**
         * Создает Holder для адаптера с категориями.
         */
        fun create(
            parent: ViewGroup,
            clickListener: (scheduleName: String, categoryName: String, id: Int) -> Unit,
        ): RepositoryCategoryHolder {
            return RepositoryCategoryHolder(
                ItemRepositoryCategoryBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                clickListener
            )
        }
    }
}