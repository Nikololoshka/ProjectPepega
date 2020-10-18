package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemRepositoryCategoryBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryCategoryItem
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.worker.ScheduleDownloadWorker

/**
 * Holder категории в репозитории.
 */
class RepositoryCategoryHolder(
    private val binding: ItemRepositoryCategoryBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val adapter = RepositoryScheduleItemAdapter { scheduleName, position ->
        if (ScheduleRepository().exists(binding.root.context, scheduleName)) {
            Snackbar.make(binding.root, R.string.schedule_editor_exists, Snackbar.LENGTH_SHORT)
                .show()
            return@RepositoryScheduleItemAdapter
        }

        ScheduleDownloadWorker.startWorker(
            binding.root.context,
            categoryName,
            scheduleName,
            bindingAdapterPosition * 1000 + position
        )

        Snackbar.make(
            binding.root,
            binding.root.context.getString(R.string.repository_start_loading, scheduleName),
            Snackbar.LENGTH_SHORT
        ).show()
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